# quartz
    动态处理定时任务，支持定时任务的动态添加 修改时间 暂停 恢复 启动等功能
    1. 创建任务 `/service/quartz/add_job`
         {
         	"jobName":"testAddJob",
         	"cronExpression":"*/5 * * * * ?",
         	"jobGroup":"testAddJobGroup",
         	"triggerName":"testAddJobTrigger",
         	"desc":"description",
         	"clazz":"com.xzy.chenbao.business.job.CommonJob",
         	"data":{"name":"aaa", "value":"bbb"}
         }
         cronExpression:cron表达式， clazz:需要执行的类名， data:需要的业务参数 在执行类中可以获取
         执行类获取参数 例如：
          public class CommonJob implements Job {
          	@Override
          	public void execute(JobExecutionContext context) throws JobExecutionException {
          		String method = context.getJobDetail().getKey().getName();
          		JSONObject data = (JSONObject) context.getJobDetail().getJobDataMap().get("data");
          	}
          }
    2. 暂停一个job（以下详见ScheduleController 和 ScheduleServiceImpl）
    3. 恢复一个job
    4. 删除一个job
    5. 立即执行这个job
    6. 更新job执行时间
    7. 查询正在执行的job列表
    8. 查询当前定时是否存在
# common
    工具包 包含加解密 异常 helper类等
# kafka
    spring-kafka 消息生产消费
    1. 生产消息 /service/producer/add
         {
         	"topic":"chenbao",
         	"msg":"测试"
         }
    2. 消费消息
         @Bean
         public KafkaListeners kafkaListeners() {
             return new KafkaListeners();
         }
         
         public class KafkaListeners {
              @KafkaListener(topics = {"chenbao"})
              public void listen(ConsumerRecord<?, ?> record) {
                  Optional<?> kafkaMessage = Optional.ofNullable(record.value());
                  if (kafkaMessage.isPresent()) {
                      Object message = kafkaMessage.get();
                      System.out.println(message);
                  }
              }
          }
# mvc 
    处理数据转换等
# mybatis
    db操作 分页 事务 等
# netflix
    基于spring-cloud 完成微服务 的注册 发现  负载  熔断等功能（未完成）
# nosql
    redisson 实现redis操作 以及分布式锁
    1. redis操作详见NosqlService
    2. 分布式锁详见LockService

