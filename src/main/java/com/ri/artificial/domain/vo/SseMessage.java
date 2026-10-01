package com.ri.artificial.domain.vo;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SSE 流式响应帧。
 * 前后台两条流式接口（{@code /admin/chat}、{@code /front/chat}）都以本结构下发每一帧，
 * 序列化后形如 {@code {"type":"content","v":"..."}}，由前端按 {@code type} 分流渲染：
 * content=正文、reasoning=深度思考过程、error=错误提示。
 * 集中在此类可避免各生产点各自 {@code Map.of("type", ..., "v", ...)} 造成结构漂移——
 * 先前前台只发 {@code {"v":...}}、后台才发 {@code {"type":...,"v":...}}，两端消费逻辑因此不一致。
 * 另外 {@code Map.of} 不接受 null 值（会抛 NPE），本类遇 null 字段仅忽略，更安全。
 *
 * @author Ri
 * @date 2026-10-01 15:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SseMessage {

    /** 正文增量 */
    public static final String CONTENT = "content";
    /** 深度思考过程增量 */
    public static final String REASONING = "reasoning";
    /** 错误提示（流中断时下发） */
    public static final String ERROR = "error";

    /** 帧类型：content / reasoning / error */
    private String type;

    /** 帧内容（正文 / 思考过程 / 错误文案） */
    private String v;

    public static SseMessage content(String v) {
        return new SseMessage(CONTENT, v);
    }

    public static SseMessage reasoning(String v) {
        return new SseMessage(REASONING, v);
    }

    public static SseMessage error(String v) {
        return new SseMessage(ERROR, v);
    }

    /** 序列化为 SSE 的 data 载荷 */
    public String toJson() {
        return JSONUtil.toJsonStr(this);
    }
}
