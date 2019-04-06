package xxx;

import xxx.AbstractRetryTasklet;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

public class ExtendTasklet extends AbstractRetryTasklet {

  /**
   * 継承したタスクレット.
   *
   * @param chunkContext ChunkContext.
   * @param contribution StepContribution.
   * @return RepeatStatus.
   * @throws Exception API失敗.
   */
  @Override
  public RepeatStatus execute(ChunkContext chunkContext, StepContribution contribution)
      throws Exception {
    // 正常終了.
    contribution.setExitStatus(ExitStatus.COMPLETED);
    return RepeatStatus.FINISHED;
  }
}
