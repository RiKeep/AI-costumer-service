package com.ri.artificial.constant;

/**
 * @author Ri
 * @date 2026-10-01 15:24
 *  在发送给AI模型的消息中，角色（role）用于区分不同的参与者。常见的角色包括：
 *  用户（user）：表示消息是由用户发送的。
 *  AI助手（assistant）：表示消息是由AI模型生成的。
 *  系统（system）：表示消息是由系统发送的，通常用于提供上下文
 */
public class MessageRole {
    /** 用户 */
    public static final String USER = "user";
    /** AI */
    public static final String ASSISTANT = "assistant";
    /** 系统 */
    public static final String SYSTEM = "system";
}
