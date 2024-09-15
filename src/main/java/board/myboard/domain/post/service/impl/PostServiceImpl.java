package board.myboard.domain.post.service.impl;

import board.myboard.domain.member.exception.MemberException;
import board.myboard.domain.member.repository.MemberRepository;
import board.myboard.domain.post.cond.PostSearchCondition;
import board.myboard.domain.post.dto.PostInfoDTO;
import board.myboard.domain.post.dto.PostPagingDTO;
import board.myboard.domain.post.dto.PostSaveDTO;
import board.myboard.domain.post.dto.PostUpdateDTO;
import board.myboard.domain.post.entity.Post;
import board.myboard.domain.post.exception.FileException;
import board.myboard.domain.post.exception.PostException;
import board.myboard.domain.post.repository.PostRepository;
import board.myboard.domain.post.service.FileService;
import board.myboard.domain.post.service.PostService;
import board.myboard.global.exception.ErrorCode;
import board.myboard.global.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {


    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    @Override
    public void save(PostSaveDTO postSaveDTO) throws FileException {
        Post post = postSaveDTO.toEntity();

        post.confirmWriter(memberRepository.findByUsername(SecurityUtil.getLoginUsername())
                .orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER)));

        postSaveDTO.uploadFile().ifPresent(
                file -> post.updateFilePath(fileService.save(file))
        );

        postRepository.save(post);
    }

    @Override
    public void update(Long id, PostUpdateDTO postUpdateDTO) throws FileException {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostException(ErrorCode.NOT_FOUND_BOARD));

        checkAuthority(post, new PostException(ErrorCode.ACCESS_DENIED));

        postUpdateDTO.title().ifPresent(post::updateTitle);
        postUpdateDTO.content().ifPresent(post::updateContent);

        if (post.getFilePath() != null) {
            fileService.delete(post.getFilePath()); // 기존에 올린 파일 지우기
        }

        postUpdateDTO.uploadFile().ifPresentOrElse(
                multipartFile -> post.updateFilePath(fileService.save(multipartFile)),
                () -> post.updateFilePath(null)
        );
    }

    @Override
    public void delete(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostException(ErrorCode.NOT_FOUND_BOARD));

        checkAuthority(post, new PostException(ErrorCode.ACCESS_DENIED));

        if (post.getFilePath() != null) {
            fileService.delete(post.getFilePath()); // 기존에 올린 파일 지우기
        }

        postRepository.delete(post);

    }

    /**
     * Post + Member 조회 -> 쿼리 1번 발생
     *
     * 댓글&대댓글 리스트 조회 -> 쿼리 1번 발생(POST ID로 찾는 것이므로, IN쿼리가 아닌 일반 where 문 발생)
     * (댓글과 대댓글 모두 Comment 클래스이므로, JPA는 구분할 방법이 없어서, 당연히 CommentList에 모두 나오는 것이 맞다)
     * 가지고 온 것을 구분해주어야 함
     *
     * 댓글 작성자 정보 조회 -> 배치사이즈를 이용했기 때문에 쿼리 1번 혹은 N/배치사이즈 만큼 발생
     */
    @Override
    public PostInfoDTO getPostInfo(Long id) {

        return new PostInfoDTO(postRepository.findWithWriterById(id)
                .orElseThrow(() -> new PostException(ErrorCode.NOT_FOUND_BOARD)));
    }

    @Override
    public PostPagingDTO getPostList(Pageable pageable, PostSearchCondition postSearchCondition) {
        return null;
    }

    private void checkAuthority(Post post, PostException postException) {
        if (!post.getWriter().getUsername().equals(SecurityUtil.getLoginUsername()))
            throw new PostException(postException.getErrorCode());
    }
}
