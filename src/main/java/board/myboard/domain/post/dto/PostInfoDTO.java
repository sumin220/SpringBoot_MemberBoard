package board.myboard.domain.post.dto;

import board.myboard.domain.comment.dto.CommentInfoDTO;
import board.myboard.domain.comment.entity.Comment;
import board.myboard.domain.member.dto.MemberInfoDTO;
import board.myboard.domain.post.entity.Post;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class PostInfoDTO {

    private Long postId; //POST의 id
    private String title;
    private String content;
    private String filePath;

    private MemberInfoDTO writeDTO; //작성자에 대한 정보

    private List<CommentInfoDTO> commentInfoDTOList; //댓글 정보들


    public PostInfoDTO(Post post) {

        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.filePath = post.getFilePath();

        this.writeDTO = new MemberInfoDTO(post.getWriter());



        /**
         * 댓글과 대댓글을 그룹짓기
         * post.getCommentList()는 댓글과 대댓글이 모두 조회된다.
         */
        Map<Comment, List<Comment>> commentListMap = post.getCommentList().stream()
                .filter(comment -> comment.getParent() != null)
                .collect(Collectors.groupingBy(Comment::getParent));


        /**
         * 댓글과 대댓글을 통해 CommentInfoDTO 생성
         */
        commentInfoDTOList = commentListMap.keySet().stream()
                .map(comment -> new CommentInfoDTO(comment, commentListMap.get(comment)))
                .toList();

    }



}
