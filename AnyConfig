package xxx;

import xxx.ExtendTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class AnyConfig {

  /** バッチID. */
  private static final String BATCH_ID = "AnyBatch";
  /** API リトライ回数. */
  protected static final int API_RETRY_COUNT = 3;

  @Autowired protected JobBuilderFactory jobBuilderFactory;
  @Autowired protected StepBuilderFactory stepBuilderFactory;

  /**
   * ジョブ.
   *
   * @return Job.
   */
  @Bean
  public Job anyJob() {
    return jobBuilderFactory
        .get(BATCH_ID)
        .incrementer(new RunIdIncrementer())
        // 実行するステップ.
        .start(anyStep())
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .on(ExitStatus.COMPLETED.getExitCode())
        .end()
        .end()
        .build();
  }

  /**
   * ステップ.
   *
   * @return Step.
   */
  @Bean
  protected Step anyStep() {
    return stepBuilderFactory
        .get("anyStep")
        .tasklet(extendTasklet().retry(API_RETRY_COUNT))
        .build();
  }

  /**
   * ExtendTasklet.
   *
   * @return ExtendTasklet.
   */
  @Bean
  protected ExtendTasklet extendTasklet() {
    return new ExtendTasklet();
  }

  /**
   * stepのリトライをする例外クラスリストを取得
   *
   * @return 例外クラスList.
   */
  @Bean
  protected List<Class<? extends Throwable>> getAllowRetryExceptions() {
    // stepのリトライをする例外クラス.
    return new ArrayList<>() {
      {
        // Httpステータスチェック.
        add(StepRetryException.class);
        // Thread.sleep 例外.
        add(InterruptedException.class);
      }
    };
  }
}
