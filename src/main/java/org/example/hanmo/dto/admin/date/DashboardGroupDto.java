package org.example.hanmo.dto.admin.date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardGroupDto {
    private String todayMatchedGroupCount;
    private String totalMatchedGroupCount;
}
