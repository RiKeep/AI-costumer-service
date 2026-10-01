package com.ri.artificial.context;

/**
 * @author Ri
 * @date 2026-10-01 15:47
 */
public class UserContext {
    private static final ThreadLocal<Integer> USER_ID_THREAD = new ThreadLocal<>();

    public static void setUserId(Integer userId) {
        USER_ID_THREAD.set(userId);
    }

    public static Integer getUserId() {
        return USER_ID_THREAD.get();
    }

    public static void remove() {
        USER_ID_THREAD.remove();
    }
}
