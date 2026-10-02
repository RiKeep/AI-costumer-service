package com.ri.artificial.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Ri
 * @date 2026-10-02 21:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SplitterInfoVO {
    private String name;
    private String description;
    private List<SplitterOption> options;
}