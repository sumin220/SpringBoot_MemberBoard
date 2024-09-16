package board.myboard.global.log;

import board.myboard.global.security.SecurityUtil;

import java.util.UUID;

public class TraceId {

    private String id;
    private int level; //깊이

    public TraceId() {
        this.id = createId();
        this.level = 0;
    }

    public TraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    private String createId() {
        try {
            SecurityUtil.getLoginUsername();
        } catch (NullPointerException | ClassCastException e) { //로그인 안 하고 접근& signUp 등일 경우 anonymouseUser가 반환->캐스팅 불가
            return String.format("[Anonymous: %S]", UUID.randomUUID().toString().substring(0,8));
        }
        return SecurityUtil.getLoginUsername();
    }

    public TraceId createNextId() {
        return new TraceId(id,level + 1);
    }

    public TraceId createPreviousId() {
        return new TraceId(id,level - 1);
    }

    public boolean isFirstLevel() {
        return level == 0;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }
}
