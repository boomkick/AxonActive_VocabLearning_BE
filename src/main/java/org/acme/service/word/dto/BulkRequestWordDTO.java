package org.acme.service.word.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BulkRequestWordDTO {
    private List<RequestWordDTO> words;
}
