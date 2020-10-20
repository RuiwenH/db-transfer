# 数据迁移小工具
* 解决SQLines Data Tool 无法将oracle的blob字段迁移至mysql
* 解决SQLines Data Tool 无法将oracle的NClob字段迁移至mysql
* 使用mybatis读取数据然后插入的方式，仅限小数据量
* mybatis读取oracle的blob字段时转换为byte[]重新插入mysql
* 目前支持：来源数据库oracle 》目标数据库mysql；来源数据库mysql 》目标数据库mysql

# 使用方式1
* 将工程中的 db-transfer-0.0.1.jar 、application.yml 、tables.txt 拷贝至一个空目录
* 修改application.yml中的数据库连接
* 修改tables.txt 中的迁移表清单
* java -jar db-transfer-0.0.1.jar --file=tables.txt
* 检测是否有error日志

# 预留问题
* 待验证无主键表，分页查询未指定order by 是否能正确读取数据（不同数据库会有不同的现象）

# 待扩展
* 源数据库，增加数据库需要在SourceMapper.xml增加相应的sql
* 目标数据库，批量insert操作是标准语句，理论上支持多种数据库，——待验证是否支持其他数据库
* 使用多线程并发迁移数据

# 测试

```
测试插入字段47个，45947条
每批200条， 52秒
每批1000条， 41秒
每批3000条， 36秒
每批5000条， 36秒
每批10000条，38秒
每批20000条，37秒 ——不建议，不同的表估计会报错
```
# 