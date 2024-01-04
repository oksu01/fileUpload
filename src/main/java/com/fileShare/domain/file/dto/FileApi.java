package com.fileShare.domain.file.dto;


import com.fileShare.domain.file.entity.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileApi {
    private String title;
    private String uniqueId;
    private String savedPath;
}
