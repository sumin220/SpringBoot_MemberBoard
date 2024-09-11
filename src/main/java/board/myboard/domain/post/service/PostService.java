package board.myboard.domain.post.service;

import board.myboard.domain.post.cond.PostSearchCondition;
import board.myboard.domain.post.dto.PostInfoDTO;
import board.myboard.domain.post.dto.PostPagingDTO;
import board.myboard.domain.post.dto.PostSaveDTO;
import board.myboard.domain.post.dto.PostUpdateDTO;
import board.myboard.domain.post.exception.PostException;

import java.awt.print.Pageable;

public interface PostService {

    /**
     * 게시글 등록
     */
    void save(PostSaveDTO postSaveDTO) throws PostException;

    /**
     * 게시글 수정
     */
    void update(Long id, PostUpdateDTO postUpdateDTO) throws PostException;

    /**
     * 게시글 삭제
     */
    void delete(Long id);

    /**
     * 게시글 1개 조회
     */
    PostInfoDTO getPostInfo(Long id);

    /**
     * 검색 조건에 따른 게시글 리스트 조회 + 페이징
     */
    PostPagingDTO getPostList(Pageable pageable, PostSearchCondition postSearchCondition);
}
