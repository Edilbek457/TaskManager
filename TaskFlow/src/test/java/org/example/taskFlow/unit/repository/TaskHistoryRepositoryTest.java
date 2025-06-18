package org.example.taskFlow.unit.repository;

import org.bson.types.ObjectId;
import org.example.taskFlow.model.oldModel.TaskHistory;
import org.example.taskFlow.repository.oldRepository.TaskHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataMongoTest
public class TaskHistoryRepositoryTest {

    @Autowired
    private TaskHistoryRepository taskHistoryRepository;

    private TaskHistory taskHistory;

    @BeforeEach
    public void setUp() {
        ObjectId Id = new ObjectId();

        taskHistory = new TaskHistory();
        taskHistory.setId(Id);
        taskHistory.setTaskId(100L);
        taskHistory.setAction("UPDATED");
        taskHistory.setPerformedBy(123L);
        taskHistory.setTimestamp(LocalDateTime.now());
        taskHistory.setDetails(Map.of("field", "value"));

        taskHistoryRepository.save(taskHistory);
    }

    @Test
    public void findTasksHistoriesById_thenReturnCorrectHistory() {
        List<TaskHistory> found = taskHistoryRepository.findTasksHistoriesById(taskHistory.getId());

        assertThat(found).isNotNull();
        assertThat(found.get(0).getAction()).isEqualTo("UPDATED");
        assertThat(found.get(0).getPerformedBy()).isEqualTo(123L);
        assertThat(found.get(0).getDetails().get("field")).isEqualTo("value");
        assertThat(found.get(0).getTaskId()).isEqualTo(taskHistory.getTaskId());
    }
}
