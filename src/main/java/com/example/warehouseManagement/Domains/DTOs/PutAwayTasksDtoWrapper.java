package com.example.warehouseManagement.Domains.DTOs;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PutAwayTasksDtoWrapper {
    @Builder.Default
    List<PutAwayTaskDto> putAwayTasks = new ArrayList<>();
}
