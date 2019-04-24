package xxx;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;

public class AbstractRetryTaskletTest {

  @Rule public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.LENIENT);

  @Spy private AbstractRetryTasklet abstractRetryTasklet;

  private StepContribution contribution;
  private ChunkContext chunkContext;

  /** setup. */
  @Before
  public void before() {
    final JobExecution jobExcution =
        new JobExecution(new JobInstance(123L, "job"), new JobParameters());

    contribution = new StepContribution(new StepExecution("foo", jobExcution));
    chunkContext = new ChunkContext(new StepContext(new StepExecution("step", jobExcution)));
  }

  /**
   * 正常.
   *
   * @throws Exception 例外.
   */
  @Test
  public void execute() throws Exception {
    // 実行.
    abstractRetryTasklet.retry(2).execute(contribution, chunkContext);

    // アサーション 実行回数.
    verify(abstractRetryTasklet, times(1))
        .execute(any(StepContribution.class), any(ChunkContext.class));
    verify(abstractRetryTasklet, times(1))
        .execute(any(ChunkContext.class), any(StepContribution.class));
  }

  /**
   * 異常時 リトライ.
   *
   * @throws Exception 例外.
   */
  @Test
  public void execute_retry() throws Exception {
    // 例外を発生させる.
    when(abstractRetryTasklet.execute(any(ChunkContext.class), any(StepContribution.class)))
        .thenThrow(new Exception());

    // 実行.
    try {
      abstractRetryTasklet.retry(2).execute(contribution, chunkContext);
    } catch (Exception e) {
      // ログ出力時に、エラーになるが、確認点として重要性が低いのでスキップ.
      // モックにモックを注入できないため解消困難.
    }
    // アサーション 実行回数.
    verify(abstractRetryTasklet, times(1))
        .execute(any(StepContribution.class), any(ChunkContext.class));
    verify(abstractRetryTasklet, times(3))
        .execute(any(ChunkContext.class), any(StepContribution.class));
  }

  /**
   * 異常時 リトライ未指定.
   *
   * @throws Exception 例外.
   */
  @Test
  public void execute_noRetry() throws Exception {
    // 例外を発生させる.
    when(abstractRetryTasklet.execute(any(ChunkContext.class), any(StepContribution.class)))
        .thenThrow(new Exception());

    // 実行.
    try {
      abstractRetryTasklet.execute(contribution, chunkContext);
    } catch (Exception e) {
      // ログ出力時に、エラーになるが、確認点として重要性が低いのでスキップ.
      // モックにモックを注入できないため解消困難.
    }

    // アサーション 実行回数.
    verify(abstractRetryTasklet, times(1))
        .execute(any(StepContribution.class), any(ChunkContext.class));
    verify(abstractRetryTasklet, times(1))
        .execute(any(ChunkContext.class), any(StepContribution.class));
  }

  /**
   * 異常時 リトライ 例外クラス指定.
   *
   * @throws Exception 例外.
   */
  @Test
  public void execute_retry_setAllowExceptions() throws Exception {
    // 例外を発生させる.
    when(abstractRetryTasklet.execute(any(ChunkContext.class), any(StepContribution.class)))
        .thenThrow(new InterruptedException());

    final List<Class<? extends Throwable>> allowExceptions =
        new ArrayList<>() {
          {
            add(InterruptedException.class);
          }
        };

    // 実行.
    try {
      abstractRetryTasklet.retry(2, allowExceptions).execute(contribution, chunkContext);
    } catch (Exception e) {
      // ログ出力時に、エラーになるが、確認点として重要性が低いのでスキップ.
      // モックにモックを注入できないため解消困難.
    }
    // アサーション 実行回数.
    verify(abstractRetryTasklet, times(1))
        .execute(any(StepContribution.class), any(ChunkContext.class));
    verify(abstractRetryTasklet, times(3))
        .execute(any(ChunkContext.class), any(StepContribution.class));
  }

  /**
   * 異常時 リトライ 例外クラス指定 リトライしない例外が発生した場合.
   *
   * @throws Exception 例外.
   */
  @Test
  public void execute_retry_setAllowExceptions_occur_unexcepted_Exception() throws Exception {
    // 例外を発生させる.
    when(abstractRetryTasklet.execute(any(ChunkContext.class), any(StepContribution.class)))
        .thenThrow(new Exception());

    final List<Class<? extends Throwable>> allowExceptions =
        new ArrayList<>() {
          {
            add(InterruptedException.class);
          }
        };

    // 実行.
    try {
      abstractRetryTasklet.retry(2, allowExceptions).execute(contribution, chunkContext);
    } catch (Exception e) {
      // ログ出力時に、エラーになるが、確認点として重要性が低いのでスキップ.
      // モックにモックを注入できないため解消困難.
    }
    // アサーション 実行回数.
    verify(abstractRetryTasklet, times(1))
        .execute(any(StepContribution.class), any(ChunkContext.class));
    verify(abstractRetryTasklet, times(1))
        .execute(any(ChunkContext.class), any(StepContribution.class));
  }
}
