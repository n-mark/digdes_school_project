package ru.digdes.school.dto.project;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdNameProjectDto {
    private Long id;
    private String name;
}
