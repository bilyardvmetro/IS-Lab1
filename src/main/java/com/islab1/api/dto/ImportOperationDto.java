package com.islab1.api.dto;

import com.islab1.entities.ImportOperation;
import com.islab1.entities.ImportStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImportOperationDto {

    private Long id;
    private String startedAt;
    private String finishedAt;
    private String username;
    private ImportStatus status;
    private int importedCount;
    private int errorCount;

    public static ImportOperationDto fromEntity(ImportOperation op) {
        return new ImportOperationDto(
                op.getId(),
                op.getStartedAt() != null ? op.getStartedAt().toString() : null,
                op.getFinishedAt() != null ? op.getFinishedAt().toString() : null,
                op.getUser() != null ? op.getUser().getUsername() : null,
                op.getStatus(),
                op.getImportedCount(),
                op.getErrorCount()
        );
    }
}
