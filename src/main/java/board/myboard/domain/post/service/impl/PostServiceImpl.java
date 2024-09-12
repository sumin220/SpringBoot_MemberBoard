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

    @Override
    public PostInfoDTO getPostInfo(Long id) {
        return null;
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
