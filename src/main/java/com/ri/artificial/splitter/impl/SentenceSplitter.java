package com.ri.artificial.splitter.impl;

import com.ri.artificial.domain.vo.SplitterOption;
import com.ri.artificial.domain.dto.SplitterForm;
import com.ri.artificial.splitter.DocumentSplitter;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Ri
 * @date 2026-10-02 21:42
 * 句子分片：按标点切句，再合并到目标长度
 */
@Component
public class SentenceSplitter implements DocumentSplitter {

    // 中英文句末标点，lookbehind 保留标点
    private static final Pattern SENTENCE_END =
            Pattern.compile("(?<=[。！？.!?])");

    @Override
    public String name() { return "段落分片"; }

    @Override
    public String description() {
        return "按句子边界切分，保证句子完整，适合问答型知识库";
    }

    @Override
    public List<SplitterOption> options() {
        return List.of(
                new SplitterOption("chunkSize", "分片长度(字符)", 500, 100, 2000),
                new SplitterOption("maxSentences", "每片最多句子数", 10, 1, 50),
                new SplitterOption("overlapSentences", "重叠句子数", 1, 0, 5)
        );
    }

    @Override
    public List<Document> split(List<Document> documents, SplitterForm params) {
        int chunkSize = params.getChunkSize();
        int maxSentences = params.getMaxSentences();
        int overlapSentences = params.getOverlapSentences();

        List<Document> result = new ArrayList<>();
        for (Document doc : documents) {
            List<String> sentences = Arrays.stream(SENTENCE_END.split(doc.getText()))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            List<String> chunks = merge(sentences, chunkSize, maxSentences, overlapSentences);
            for (int i = 0; i < chunks.size(); i++) {
                Map<String, Object> meta = new HashMap<>(doc.getMetadata());
                meta.put("chunk_type", "sentence");
                meta.put("chunk_index", i);
                result.add(new Document(chunks.get(i), meta));
            }
        }
        return result;
    }

    private List<String> merge(List<String> sentences, int chunkSize,
                               int maxSentences, int overlapSentences) {
        List<String> chunks = new ArrayList<>();
        List<String> current = new ArrayList<>();
        int currentLen = 0;

        for (String sentence : sentences) {
            boolean overSize = currentLen + sentence.length() > chunkSize && !current.isEmpty();
            boolean overCount = current.size() >= maxSentences;

            if (overSize || overCount) {
                chunks.add(String.join("", current));

                // 保留最后 N 句作为重叠
                List<String> tail = current.size() > overlapSentences
                        ? current.subList(current.size() - overlapSentences, current.size())
                        : current;
                current = new ArrayList<>(tail);
                currentLen = current.stream().mapToInt(String::length).sum();
            }
            current.add(sentence);
            currentLen += sentence.length();
        }
        if (!current.isEmpty()) {
            chunks.add(String.join("", current));
        }
        return chunks;
    }
}