package com.ri.artificial.domain.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author Ri
 * @date 2026-08-06 16:40
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class KnowledgePageQuery extends PageQuery {
    private String fileName;
}
