##### # 架构

![](http://baihonghua.cn/20200416115200.png)

##### # Room实战

需要创建的数据库表：

![](http://baihonghua.cn/20200416124212.png)

转换的 Kt 如下：

![](http://baihonghua.cn/20200416124417.png)

- **@Entity** 每被该注解的类表示一个 `SQLite` 表，可以通过 `tableName` 来指定该 `SQLite` 的表名；

- **@PrimaryKey** 每张数据表都需要一个主键，上图为了简单把 `word` 作为主键；

- **@ColumnInfo(name = "word")** 指定列的名字在数据表中；

上图也可以有以下的写法：

![](http://baihonghua.cn/20200416130730.png)

DAO 类的编写

![](http://baihonghua.cn/20200416132107.png)

Room 数据库是基于 SQLite 数据库的，在 `SQLiteOpenHelper` 基础上面做封装。Room 的操作不允许在主线程上面进行操作，当 Room 查询返回 LiveData，查询操作会自动开启一条异步线程。

##### # 实现Room数据库

`Room Database` 的类文件必须是声明 `abstract` 关键字和继承于 `RoomDatabase`, 通常使用单例模式向整个 App 提供访问的接口。

当修改数据库表，需要更新数据库的版本号。

![](http://baihonghua.cn/20200417104010.png)








 