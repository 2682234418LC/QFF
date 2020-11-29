# 						SSM框架学习

## Mybatis：

概念：mybatis是一个数据访问层框架，封装了jdbc操作的很多细节，使用了ORM思想实现了结果集封装

#### ORM：

* Object	Relational	Mappging	对象映射关系

* 就是把数据库表和实体类及实体类的属性对应起来，让我们操作实体类就可以实现操作数据库表

#### mybatis的环境搭建：

* 创建maven工程并导入坐标

* 创建实体类和dao的接口

* 创建Mybatis的主配置文件

* 创建映射配置文件

  * 映射配置文件的mapper标签namespace属性的取值必须是dao接口的全限定类名

  * ```java
    <mapper namespace="dao.UserDao">
        <!--配置查询所有-->
        <select id="findAll">
            select * from user
        </select>
    </mapper>
    ```

  * 遵从这种方法在开发中就无须再写dao的实现类

  #### mybatis的执行步骤：

  * 读取配置文件
  * 创建SqlSessionFactory工厂
  * 创建SqlSession
  * 创建Dao接口的代理对象
  * 执行dao中的方法
  * 释放资源
    * 不要忘记在映射文件配置中告知mybatis要封装到哪个实体类中
    * mybatis基于注解的：
    * 在dao接口的方法上使用@Select，指定sql语句
    * 在SqlMapConfig.xml的mapper配置时，使用class指定dao接口的全限定类名

![](笔记_img/入门案例的分析.png)



自定义mybatis分析：在使用代理dao的方式实现增删改查

* 创建了代理对象
* 在代理对象中调用selectList

![](笔记_img/自定义Mybatis分析.png)

#### OGNL表达式：

Object	Gaphic	Navigation	Language：它是通过对象的取值方法来获取数据，在写法上省略get

如：我们获取用户名称：user.getUsername------>user.username,使用#{}或者${value}来完成获取

mybatis中因为设置了parameterType，提供了属性所属的类，此时不需要再写对象名

#### 传递pojo包装对象：

开发中通过 pojo 传递查询条件 ，查询条件是综合的查询条件，不仅包括用户查询条件还包括其它的查

询条件（比如将用户购买商品信息也作为查询条件），这时可以使用包装对象传递输入参数。

Pojo 类中包含 pojo。

需求：根据用户名查询用户信息，查询条件放到 QueryVo 的 user 属性中。

```java
/**
 * <p>Title: QueryVo</p>
 * <p>Description: 查询条件对象</p>
 */
public class QueryVo implements Serializable {
    private User user;
    public User getUser() {
        return user; }
    public void setUser(User user) {
        this.user = user; } 
}
```

```java
/**
 * <p>Title: IUserDao</p>
 * <p>Description: 用户的业务层接口</p>
 */
public interface IUserDao {
    /**
     * 根据 QueryVo 中的条件查询用户
     * @param vo
     * @return
     */
    @Select("select * from user where username like #{user.username}")
    List<User> findByVo(QueryVo vo);
}
```

```java
    @Test
    public void testFindByQueryVo() {
        QueryVo vo = new QueryVo();
        User user = new User();
        user.setUserName("%王%");
        vo.setUser(user);
        List<User> users = userDao.findByVo(vo);
        for(User u : users) {
            System.out.println(u);
        } 
}
```

#### ResultMap结果类型：

当实体类属性和数据库列名不匹配：

方法一：

resultMap 标签可以建立查询的列名和实体类的属性名称不一致时建立对应关系。从而实现封装。

在 select 标签中使用 resultMap 属性指定引用即可。同时 resultMap 可以实现将查询结果映射为复杂类

型的 pojo，比如在查询结果映射对象中包括 pojo 和 list 实现一对一查询和一对多查询

```java
type 属性：指定实体类的全限定类名
id 属性：给定一个唯一标识，是给查询 select 标签引用用的。
	--> <resultMap type="com.itheima.domain.User" id="userMap"> <id column="id" property="userId"/>
<result column="username" property="userName"/>
<result column="sex" property="userSex"/>
<result column="address" property="userAddress"/>
<result column="birthday" property="userBirthday"/>
</resultMap>
        id 标签：用于指定主键字段
        result 标签：用于指定非主键字段
        column 属性：用于指定数据库列名
        property 属性：用于指定实体类属性名称
```

方法二：

使用别名查询：

```java
<!-- 配置查询所有操作 --> <select id="findAll" resultType="com.itheima.domain.User">
        select id as userId,username as userName,birthday as userBirthday,
        sex as userSex,address as userAddress from user
</select>
```

#### Mybatis中Dao实现类的实现过程分析

<img src="笔记_img/非常重要的一张图-分析编写dao实现类Mybatis的执行过程(1).png" style="zoom: 200%;" />

#### 不写实现类的分析：

![](笔记_img/非常重要的一张图-分析代理dao的执行过程.png)

#### Properties属性：

<!-- 配置连接数据库的信息

resource 属性：用于指定 properties 配置文件的位置，要求配置文件必须在类路径下

resource="jdbcConfig.properties"

url 属性：

URL： Uniform Resource Locator 统一资源定位符

如：http://localhost:8080/mystroe/CategoryServlet 

URL 协议  主机 端口 URI

URI：Uniform Resource Identifier 统一资源标识符

如：/mystroe/CategoryServlet

它是可以在 web 应用中唯一定位一个资源的路径

--> 

<properties >

url= file:///D:/IdeaProjects/day02_eesy_01mybatisCRUD/src/main/resources/jdbcConfig.prop

</properties>

此时我们的datasource标签就变成了引用上面的配置：

<dataSource type=*"POOLED"*> 

<property name=*"driver"* value=*"**${jdbc.driver}**"*/>

<property name=*"url"* value=*"**${jdbc.url}**"*/>

<property name=*"username"* value=*"**${jdbc.username}**"*/>

<property name=*"password"* value=*"**${jdbc.password}**"*/>

</dataSource>

#### Mybatis连接池：

* 连接池就是用于存储连接的一个容器
* 容器其实就是一个集合对象，i该集合必须是线程安全的，不能两个线程拿到统─连接
* 该集合还必须实现队列的特性:先进先出

Mybatis 将它自己的数据源分为三类：

* 不使用连接池的数据源 	  UNPOOLED ：使用传统的获取连接的方式，实现了javax.sql.DataSource接口，

  没用使用池的思想

* 使用连接池的数据源          POOLED ：使用传统的javax.sql.DataSource规范中的连接池

* 使用 JNDI 实现的数据源     JNDI ：采用服务器提供的JNDI技术实现，来获取DataSource对象，不同的服务器

  所能拿到的DataSource是不一样的

  * 注意：如果不是web或者maven的war工程是不能使用过的
  * 我们使用的Tomcat服务器，采用的连接池就是dbcp连接池

![](笔记_img/mybatis_pooled的过程.png)

Mybatis事务：它是通过sqlsession的commit方法和rollback方法实现事务的提交和回滚

Mybatis的多表查询：

* 示例：用户和账户
  * 一个用户可以有多个账户
  * 一个账户只能属于一个用户（多个账户也可以属于同一个用户）
* 步骤：
  * 建立两张表：用户表，账户表，让用户表和账户表之间具备一对多的关系：需要使用外键在账户表中添加
  * 建立两个实体类：用户实体类和账户实体类体现出来一对多的关系
  * 建立两个配置文件，用户和账户的配置文件
  * 实现配置：
    * 当我们查询用户时，可以同时得到用户下所包含的账户信息
    * 当我们查询账户时，可以同时得到账户的所属用户信息
* 多对多查询：
  * 建立两张表：用户表，角色表，让用户表和角色表之间具备多对多的关系，各自包含对方的一个集合引用
  * 当我们查询用户时，可以同时得到用户下所包含的角色信息
  * 当我们查询角色时，可以同时得到角色的所属用户信息

#### Mybatis中的延迟加载：

* 问题：在一对多中，当我们有一个用户，它有100个账户。
  	      在查询用户的时候，要不要把关联的账户查出来？
        	      在查询账户的时候，要不要把关联的用户查出来？
        		  在查询用户时，用户下的账户信息应该是，什么时候使用，什么时候查询的。
        	在查询账户时，账户的所属用户信息应该是随着账户查询时一起查询出来。

* 什么是延迟加载：
  * 在真正使用数据时才发起查询，不用的时候不查询。按需加载（懒加载）
* 什么是立即加载：
  * 不管用不用，只要一调用方法，马上发起查询。

* 在对应的四种表关系中：一对多，多对一，一对一，多对多
  * 一对多，多对多：通常情况下我们都是采用延迟加载。
  * 多对一，一对一：通常情况下我们都是采用立即加载。

#### Mybatis中的缓存：

* 什么是缓存：存在于内存中的临时数据。
* 为什么使用缓存：减少和数据库的交互次数，提高执行效率。
* 什么样的数据能使用缓存，什么样的数据不能使用
  * 适用于缓存：
    * 经常查询并且不经常改变的。
    * 数据的正确与否对最终结果影响不大的。
  * 不适用于缓存：
    * 经常改变的数据
    * 数据的正确与否对最终结果影响很大的。
    * 例如：商品的库存，银行的汇率，股市的牌价。

Mybatis中的一级缓存和二级缓存：

* 一级缓存：
  			它指的是Mybatis中SqlSession对象的缓存。
        			当我们执行查询之后，查询的结果会同时存入到SqlSession为我们提供一块区域中。
        			该区域的结构是一个Map。当我们再次查询同样的数据，mybatis会先去sqlsession中
        			查询是否有，有的话直接拿出来用。
        			当SqlSession对象消失时，mybatis的一级缓存也就消失了。
  * 一级缓存是 SqlSession 范围的缓存，当调用 SqlSession 的修改，添加，删除，commit()，close()等方法时，就会清空一级缓存。
* 二级缓存:
  		它指的是Mybatis中SqlSessionFactory对象的缓存。由同一个SqlSessionFactory对象创建的SqlSession共享其缓存
    		二级缓存的使用步骤：
    			第一步：让Mybatis框架支持二级缓存（在SqlMapConfig.xml中配置）
    			第二步：让当前的映射文件支持二级缓存（在IUserDao.xml中配置）
    			第三步：让当前的操作支持二级缓存（在select标签中配置）
* 二级缓存中存放的内容是数据，而不是对象

#### **Mybatis注解开发：**

当User用户类的属性名与Mysql中的属性名称不一致时

可以使用Results注解来配置：

```java
@Select("select * from user")
@Results(id="userMap",
        value= {
                @Result(id=**true**,column="id",property="userId"),
                @Result(column="username",property="userName"),
                @Result(column="sex",property="userSex"),
                @Result(column="address",property="userAddress"),
                @Result(column="birthday",property="userBirthday")
        })
```

@Results中的id是唯一标识，若想使用在其他的方法上，则需要在方法上加上注解@ResultMap(value={"id的值"})

**@Results** **注解**

**代替的是标签****<resultMap>**

该注解中可以使用单个@Result 注解，也可以使用@Result 集合

@Results（{@Result（），@Result（）}）或@Results（@Result（））

@Resutl 注解

**代替了** <id>标签和<result>标签

**@Result** **中 属性介绍：**

id 是否是主键字段

column 数据库的列名

property 需要装配的属性名

one 需要使用的@One 注解（@Result（one=@One）（）））

many 需要使用的@Many 注解（@Result（many=@many）（）））

**@One** **注解（一对一）**

**代替了****<assocation>****标签，是多表查询的关键，在注解中用来指定子查询返回单一对象。**

**@One** **注解属性介绍：**

**select** **指定用来多表查询的** **sqlmapper**

fetchType 会覆盖全局的配置参数 lazyLoadingEnabled。。

使用格式：

@Result(column=" ",property="",one=@One(select=""))

**@Many** **注解（多对一）**

**代替了****<**Collection**>****标签****,****是是多表查询的关键，在注解中用来指定子查询返回对象集合。**

注意：聚集元素用来处理“一对多”的关系。需要指定映射的 Java 实体类的属性，属性的 javaType（一般为 ArrayList）但是注解中可以不定义；

使用格式：@Result(property="",column="",many=@Many(select=""))

```java
/**
 * 查询所有用户
 * @return
 */
@Select("select * from user")
@Results(id = "userMap", value = {
        @Result(id = true, column = "id", property = "id"),
        @Result(column = "username", property = "username"),
        @Result(column = "address", property = "address"),
        @Result(property = "birthday", column = "birthday"),
        @Result(property = "accounts", column = "id",
                many = @Many(select = "dao.IAccountDao.findAccountByUid", fetchType = FetchType.LAZY))
})
List<User> findAll();

  /**
     * 根据用户id查询账户信息
     * @param uid
     * @return
     */
@Select("select * from account where uid=#{id}")
List<Account> findAccountByUid(Integer uid);
```



## Spring：

#### 简介：

Spring 是分层的 Java SE/EE 应用 full-stack 轻量级开源框架，以 IOC（Inverse Of Control：反转控制）和 AOP（Aspect Oriented Programming：面向切面编程）为内核，提供了展现层 Spring MVC 和持久层 Spring JDBC 以及业务层事务管理等众多的企业级应用技术，还能整合开源世界众多著名的第三方框架和类库，逐渐成为使用最多的 Java EE 企业应用开源框架。

**方便解耦，简化开发**

通过 Spring 提供的 IOC 容器，可以将对象间的依赖关系交由 Spring 进行控制，避免硬编码所造

成的过度程序耦合。用户也不必再为单例模式类、属性文件解析等这些很底层的需求编写代码，可

以更专注于上层的应用。

**AOP** **编程的支持**

通过 Spring 的 AOP 功能，方便进行面向切面的编程，许多不容易用传统 OOP 实现的功能可以通过 AOP 轻松应付。

**声明式事务的支持**

可以将我们从单调烦闷的事务管理代码中解脱出来，通过声明式方式灵活的进行事务的管理，提高开发效率和质量。

**方便程序的测试**

可以用非容器依赖的编程方式进行几乎所有的测试工作，测试不再是昂贵的操作，而是随手可做的事情。

**方便集成各种优秀框架**

Spring 可以降低各种框架的使用难度，提供了对各种优秀框架（Struts、Hibernate、Hessian、Quartz等）的直接支持。

**降低** **JavaEE API** **的使用难度**

Spring 对 JavaEE API（如 JDBC、JavaMail、远程调用等）进行了薄薄的封装层，使这些 API 的使用难度大为降低。

**Java** **源码是经典学习范例**

Spring 的源代码设计精妙、结构清晰、匠心独用，处处体现着大师对 Java 设计模式灵活运用以及对 Java 技术的高深造诣。它的源代码无意是 Java 技术的最佳实践的范例。

![](笔记_img/三层架构.bmp)

#### 耦合：

**什么是程序的耦合**

耦合性(Coupling)，也叫耦合度，是对模块间关联程度的度量。耦合的强弱取决于模块间接口的复杂性、调用模块的方式以及通过界面传送数据的多少。模块间的耦合度是指模块之间的依赖关系，包括控制关系、调用关系、数据传递关系。模块间联系越多，其耦合性越强，同时表明其独立性越差( 降低耦合性，可以提高其独立性)。耦合性存在于各个领域，而非软件设计中独有的，但是我们只讨论软件工程中的耦合。在软件工程中，耦合指的就是就是对象之间的依赖性。对象之间的耦合越高，维护成本越高。因此对象的设计应使类和构件之间的耦合最小。软件设计中通常用耦合度和内聚度作为衡量模块独立程度的标准。**划分模块的一个准则就是高内聚低耦合。**

**它有如下分类：**

1. 内容耦合。当一个模块直接修改或操作另一个模块的数据时，或一个模块不通过正常入口而转入另一个模块时，这样的耦合被称为内容耦合。内容耦合是最高程度的耦合，应该避免使用之。

2. 公共耦合。两个或两个以上的模块共同引用一个全局数据项，这种耦合被称为公共耦合。在具有大量公共耦合的结构中，确定究竟是哪个模块给全局变量赋了一个特定的值是十分困难的。

3. 外部耦合 。一组模块都访问同一全局简单变量而不是同一全局数据结构，而且不是通过参数表传递该全局变量的信息，则称之为外部耦合。

4.  控制耦合 。一个模块通过接口向另一个模块传递一个控制信号，接受信号的模块根据信号值而进行适当的动作，这种耦合被称为控制耦合。

5. 标记耦合 。若一个模块 A 通过接口向两个模块 B 和 C 传递一个公共参数，那么称模块 B 和 C 之间存在一个标记耦合。

6.  数据耦合。模块之间通过参数来传递数据，那么被称为数据耦合。数据耦合是最低的一种耦合形式，系统中一般都存在这种类型的耦合，因为为了完成一些有意义的功能，往往需要将某些模块的输出数据作为另一些模块的输入数据。

7. 非直接耦合 。两个模块之间没有直接关系，它们之间的联系完全是通过主模块的控制和调用来实现的。

**总结：**

耦合是影响软件复杂程度和设计质量的一个重要因素，在设计上我们应采用以下原则：如果模块间必须存在耦合，就尽量使用数据耦合，少用控制耦合，限制公共耦合的范围，尽量避免使用内容耦合。

**程序的耦合：**

程序之间的依赖关系：

* 类之间的依赖关系
* 方法间的依赖关系

**解耦：**降低程序间的依赖关系，实际开发中，要做到，编译期不依赖，运行时才依赖

* 使用反射创建对象，避免使用new关键字
* 通过读取配置文件来获取要创建的对象全限定类名

**内聚与耦合**

内聚标志一个模块内各个元素彼此结合的紧密程度，它是信息隐蔽和局部化概念的自然扩展。内聚是从功能角度来度量模块内的联系，一个好的内聚模块应当恰好做一件事。它描述的是模块内的功能联系。耦合是软件结构中各模块之间相互连接的一种度量，耦合强弱取决于模块间接口的复杂程度、进入或访问一个模块的点以及通过接口的数据。 程序讲究的是低耦合，高内聚。就是同一个模块内的各个元素之间要高度紧密，但是各个模块之间的相互依存度却要不那么紧密。内聚和耦合是密切相关的，同其他模块存在高耦合的模块意味着低内聚，而高内聚的模块意味着该模块同其他模块之间是低耦合。在进行软件设计时，应力争做到高内聚，低耦合。

#### 基于XML配置：

步骤：

1. 导入必备的jar包，依赖
2. 创建相应的xml文件
3. 让spring管理资源，在配置文件中配置service和dao

#### Spring基于XML和IOC的细节：

**工厂类的结构图：**
![](笔记_img/spring工厂类结构图.png)

**BeanFactory和ApplicationContext的区别：**

```java
/**
* 获取spring容器的ioc核心容器，并根据id获取对象
*
* ApplicationContext的三个常用实现类
*      ClassPathXmlApplicationContext：可以加载类路径下的配置文件，要求配置文件必须在类路径下，不在的话
**  加载不了
*      FileSystemXmlApplicationContext 可以加载磁盘任意路径下的配置文件，必须有访问权限
*      AnnotationConfigApplicationContext 它是用于读取注解创建容器
*
* 核心容器的两个接口引发出的问题
* ApplicationContext：      单例对象适用      开发中多采用此接口来创建容器对象
*          它在构建核心容器时，创建对象采取的策略是采用立即加载的方式，只要一读取完配置文件马上就能创建配置文
*		   件中配置的对象。
* BeanFactory：          多例对象适用
*        它在创建容器的时候，创建对象采用延迟加载的方式，什么时候根据id获取对象，什么时候才真正的创建爱你对象
*/
```

#### Bean标签：

**作用：**

用于配置对象让 spring 来创建的，默认情况下它调用的是类中的无参构造函数。如果没有无参构造函数则不能创建成功。

**属性：**

id：给对象在容器中提供一个唯一标识。用于获取对象。

class：指定类的全限定类名。用于反射创建对象。默认情况下调用无参构造函数。

scope：指定对象的作用范围。

* singleton :默认值，单例的
* prototype :多例的
* request :WEB 项目中,Spring 创建一个 Bean 的对象,将对象存入到 request 域中
*  session :WEB 项目中,Spring 创建一个 Bean 的对象,将对象存入到 session 域中
* global session WEB 项目中,应用在 Portlet 环境.如果没有 Portlet 环境那么globalSession 相当于 session

* init-method：指定类中的初始化方法名称
* destroy-method：指定类中销毁方法名称

**bean的作用范围和生命周期：**

单例对象：scope="singleton"	一个应用只有一个对象的实例。它的作用范围就是整个引用。

生命周期：

* 对象出生：当应用加载，创建容器时，对象就被创建了
* 对象活着：只要容器在，对象一直活着
* 对象死亡：当应用卸载，销毁容器时，对象就被销毁了

多例对象：scope="prototype"    每次访问对象时，都会重新创建对象实例

生命周期：

* 对象出生：当使用对象时，创建新的对象实例
* 对象活着：只要对象在使用中，就一直活着
* 对象死亡：当对象长时间不用时，被 java 的垃圾回收器回收了

#### 依赖注入：

依赖注入：**Dependency Injection**。它是 spring 框架核心 ioc 的具体实现。我们的程序在编写时，通过控制反转，把对象的创建交给了 spring，但是代码中不可能出现没有依赖的情况。ioc 解耦只是降低他们的依赖关系，但不会消除。

例如：我们的业务层仍会调用持久层的方法。那这种业务层和持久层的依赖关系，在使用 spring 之后，就让 spring 来维护了。简单的说，就是坐等框架把持久层对象传入业务层，而不用我们自己去获取。

```java
构造函数注入：
    使用的标签：constructor-arg
    标签出现的位置：bean标签的内部
    标签的属性
    type:用于指定要注入数据的数据类型，该数据的数据类型也是构造函数中某个或某些参数的类型
    index：用于指定要注入的数据给构造函数中指定索引位置的参数赋值，索引的位置是从0开始的
    name：用于指定给构造函数中给指定名称的参数赋值       常用的
    value：用于提供基本类型和String类型数据
    ref：指定其他的bean类型数据，指的就是在spring的ioc核心容器中出现过的bean对象

    优势：在获取bean对象时，注入数据时必须的操作，否则对象无法创建成功
    弊端：改变了这个bean对象的实例化方式，使我们在创建对象时，如果用不到这些数据，也必须提供
    <bean id="accountService" class="dao.impl.AccountDaoImpl">
    <constructor-arg name="name" value="test"></constructor-arg>
    <constructor-arg name="age" value="18"></constructor-arg>
    <constructor-arg name="birthday" ref="now"></constructor-arg>
    </bean>
    <!--配置一个日期对象-->
	<bean id="now" class="java.util.Date"></bean>	
```

```java
set方法注入          更常用的方式
     涉及的标签：property
     出现的位置：bean标签的内部
     标签的属性：
         name：用于指定注入时调用的set方法名称
         value：用于提供基本类型和String类型数据
         ref：指定其他的bean类型数据，指的就是在spring的ioc核心容器中出现过的bean对象

         优势：创建对象时没有明确的限制，可以直接使用默认的构造参数
         弊端：如果有某个成员必须优质，则获取对象时有可能set方法没有执行
<bean id="accountService" class="dao.impl.AccountDaoImpl">
     <property name="name" value="test"></property>
     <property name="age" value="18"></property>
     <property name="birthday" ref="now"></property>
</bean>
 <bean id="now" class="java.util.Date"></bean>

```

```java
复杂类型的注入/集合类型的注入
<bean id="accountService" class="dao.impl.AccountDaoImpl">
       <property name="myList">
           <list>      set/array/map..
               <value>AAAA</value>
               <value>bbbb</value>
               <value>cccc</value>
           </list>
       </property>
 </bean>
```



#### 基于注解的配置：

```java
/**
* 用于创建对象的
*      他们的作用就和在XML配置文件中编写一个bean标签实现的功能是一样的
*   Component：
*      作用：用于把当前类对象存入spring容器中
*      属性：value：用于指定bean的id，当我们不写默认是当前类，首字母小写
*   Controller：一半用于表现层
*   Service：一般用于业务层
*   Repository：一般用于持久层
*   以上三个注解他们的作用和属性与Component是一模一样
*   他们三个spring框架为我们提供明确的三层使用注解
*   使我们的三层对象更加清晰
* 用于注入数据的
*      他们的作用就和XML配置文件中bean标签中写一个properties标签是一样的
*      Autowired:
*          作用:自动按照类型注入，只要容器中有唯一的一个bean对象类型和要注入的变量类型匹配，就可以注入成功
*              如果IOC容器中没有任何bean的类型和要注入的变量类型匹配，则报错。
*              如果IOC容器中有多个类型匹配时：
*          出现位置：可以是变量上，也可以是方法上
*          细节：在注解注入时，set方法就不是必须的了
*      Qualifier：
*          作用：在按照类中注入的基础之上按照名称注入，它在给类成员注入时不能单独使用，但是给方法参数注入时可以
*          属性：value：用于注入属性bean的id
*	 @Bean								给方法参数注入
*    public QueryRunner createQueryRunner(@Qualifier("ddwadawdawwada") DataSource dataSource) {
*       return new QueryRunner(dataSource);
*   }
*
*    @Bean(name = "ddwadawdawwada")
*    @Scope("prototype")
*    public DataSource createDataSource() {
*      
*	Resource：
*          作用：直接按照bean的id注入，它可以独立使用
*          属性：name：用于指定id
*
*      以上三个注解都只能注入其他bean类型数据，而基本类型和String类型无法使用上述注解实现
*      集合类型的注入只能通过xml来实现
*
*      Value：作用：用于注入基本类型和String类型的数据
*             属性：value：用于指定数据的值，它可以使用Spring中的EL表达式
*             ${表达式}
* 用于改变作用范围的
*      他们的作用范围就和bean标签中的使用scope属性实现是一样的
*      Scope：作用：用于指定bean的作用范围
*             属性：value：用于指定范围的取值， 常用取值： singleton    prototype
* 和生命周期相关的
*      他们的作用就和在bean标签中使用init-method和destroy-method是一样的
*      PreDestroy：用于指定销毁方法
*      ClassPathXmlApplicationContext as = new ClassPathXmlApplicationContext("bean.xml");
*      as.close()
*      子类才可以调用自己的方法
*      PostConstruct：用于指定初始化方法
*
*/
```



#### Spring管理对象 / 纯注解配置：

```java
/**
 * 该类是一个配置类，他的作用和bean.xnl是一样的
 * spring 中的新注解
 *
 * Configuration
 *      作用：指定当前类是一个配置类
 *      细节：当配置类作为AnnotationConfigApplicationContext对象创建的参数时，该注解可以不写
 *
 * ComponentScan
 *      作用：用于通过注解指定Spring在创建容器时要扫描的包
 *      属性：value：它和basePackages的作用是一样的，都是用于指定创建容器时要扫描的包
 *          等同于<context:component-scan base-package="ui"></context:component-scan>
 *
 * Bean
 *      作用：用于把当前方法的返回值作为bean对象存入IOC容器中
 *      属性：name：用于之指定bean的id，默认是当前方法的名称
 *      细节：当我们用注解来配置方法时，如果方法有参数，spring的框架会去容器中查找有没有可用的bean对象。
 *              查找的方式和Autowired注解的作用是一样的
 *
 * import
 *      用于导入其他的配置类
 *      属性:value：作用用于指定其他配置类的字节码文件
 *              当我们使用import的注解之后，有import注解的类就是主配置类而导入的都是子配置类
 *
 * PropertySource
 *      作用：用于指定properties文件的位置
 *      属性：value指定文件名称和路径
 *           classpath，表示类路径
 */


//当配置类作为AnnotationConfigApplicationContext对象创建的参数时，可以不写注解Configuration
@ComponentScan(basePackages = {"ui", "dao", "domain", "service"})
@Import(JdbcConfig.class)
@PropertySource("classpath:jdbcConfig.properties")
public class SpringConfiguration {

}

/**
 * 和spring连接数据库相关的配置类
 */
public class JdbcConfig {

    @Value("${jdbc.driver}")
    private String driver;
    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;

    /**
     * 用于创建一个QueryRunner对象
     * 给方法参数注入
     * @param dataSource
     * @return
     */
    @Bean
    public QueryRunner createQueryRunner(@Qualifier("ddwadawdawwada") DataSource dataSource) {
        return new QueryRunner(dataSource);
    }

    @Bean(name = "ddwadawdawwada")
    @Scope("prototype")
    public DataSource createDataSource() {
        try {
            ComboPooledDataSource ds = new ComboPooledDataSource();
            ds.setDriverClass(driver);
            ds.setJdbcUrl(url);
            ds.setUser(username);
            ds.setPassword(password);
            return ds;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```



**Spring整合junit配置：**

```java
/**
 *      1.导入spring整合junit的jar包
 *      2.使用junit提供的一个注解把原有的main方法替换了，替换成sprig提供的
 *          @Runwith
 *      3.告知sprig的运行器，spring的IOC创建时基于xml还是注解，并且说明位置
 *          @ContextConfiguration
 *              Locations:指定xml文件的位置，加上classpath关键字，表示在类路径下
 *          @ContextConfiguration(locations = "classpath:bean.xml")
 *              classes：指定注解类所在的位置
 *     细节：当我们使用spring 5.x版本的时候，要求junit的jar包必须是4.12及以上
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class AccountServiceTest {

    @Autowired
    private AccountService as;

    @Test
    public void testFindAll() {
        List<Account> accounts = as.findAllAccount();
        for (Account account : accounts) {
            System.out.println(account);
        }
    }
}
```



#### 动态代理：

**问题描述：**当我们使用了 connection 对象的 setAutoCommit(true)此方式控制事务，如果我们每次都执行一条 sql 语句，没有问题，但是如果业务方法一次要执行多条 sql语句，这种方式就无法实现功能了

**解决方法：**让业务层来控制事务的提交和回滚。

**新的问题产生：**业务层方法变得臃肿了，里面充斥着很多重复代码。并且业务层方法和事务控制方法耦合了。试想一下，如果我们此时提交，回滚，释放资源中任何一个方法名变更，都需要修改业务层的代码，况且这还只是一个业务层实现类，而实际的项目中这种业务层实现类可能有十几个甚至几十个。

**解决方法：**动态代理解决重复的代码块

**使用** **JDK** **官方的** **Proxy** **类创建代理对象：**

```java
/**
 * 用于创建service的代理对象的工厂
 */
public class BeanFactory {
    private IAccountService accountService;
    private TransactionManager tsManager;

    public void setTsManager(TransactionManager tsManager) {
        this.tsManager = tsManager;
    }

    public final void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 获取service的代理对象
     *
     * @return
     */
    public IAccountService getAccountService() {
        return (IAccountService) Proxy.newProxyInstance(accountService.getClass().getClassLoader(),
                accountService.getClass().getInterfaces(), new InvocationHandler() {
                    /**
                     * 添加事务的支持
                     * @param proxy
                     * @param method
                     * @param args
                     * @return
                     * @throws Throwable
                     */
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object rtValue = null;
                        try {
                            //1.开启事务
                            tsManager.begainTransaction();
                            //2.执行操作
                            rtValue = method.invoke(accountService, args);
                            //3.提交事务
                            tsManager.commit();
                            //4.返回结果
                            return rtValue;
                        } catch (Exception e) {
                            //5.回滚事务
                            tsManager.rollback();
                            throw new RuntimeException(e);
                        } finally {
                            //6.释放连接
                            tsManager.release();
                        }
                    }
                });
    }
}
```

**使用** **CGLib** **的** **Enhancer** **类创建代理对象：**

```java
public class Client {
    /**
     * 基于子类的动态代理
     * 要求：
     * 被代理对象不能是最终类
     * 用到的类：
     * Enhancer
     * 用到的方法：
     * create(Class, Callback)
     * 方法的参数：
     * Class：被代理对象的字节码
     * Callback：如何代理
     * @param args
     */
    public static void main(String[] args) {
        final Actor actor = new Actor();
        Actor cglibActor = (Actor) Enhancer.create(actor.getClass(),
                new MethodInterceptor() {
                    /**
                     * 执行被代理对象的任何方法，都会经过该方法。在此方法内部就可以对被代理对象的任何
                     方法进行增强。
                     *
                     * 参数：
                     * 前三个和基于接口的动态代理是一样的。
                     * MethodProxy：当前执行方法的代理对象。
                     * 返回值：
                     * 当前执行方法的返回值
                     */
                    @Override
                    public Object intercept(Object proxy, Method method, Object[] args,
                                            MethodProxy methodProxy) throws Throwable {
                        String name = method.getName();
                        Float money = (Float) args[0];
                        Object rtValue = null;
                        if("basicAct".equals(name)){
				//基本演出
                            if(money > 2000){
                                rtValue = method.invoke(actor, money/2);
                            } }
                        if("dangerAct".equals(name)){
				//危险演出
                            if(money > 5000){
                                rtValue = method.invoke(actor, money/2);
                            } }
                        return rtValue;
                    }
                });
        cglibActor.basicAct(10000);
        cglibActor.dangerAct(100000);
    } 
}
```

#### AOP：

AOP：全称是 Aspect Oriented Programming 即：面向切面编程。

简单的说它就是把我们程序重复的代码抽取出来，在需要执行的时候，使用动态代理的技术，在不修改源码的基础上，对我们的已有方法进行增强。

**在 spring 中，框架会根据目标类是否实现了接口来决定采用哪种动态代理的方式。**

**作用：**在程序运行期间，不修改源码对已有方法进行增强。

**优势：**

* 减少重复代码

* 提高开发效率

* 维护方便

**AOP** **的相关术语：**

* Joinpoint(连接点):所谓连接点是指那些被拦截到的点。在 spring 中,这些点指的是方法,因为 spring 只支持方法类型的连接点。
  * 业务层中所有的方法都是连接点

* Pointcut(切入点):所谓切入点是指我们要对哪些 Joinpoint 进行拦截的定义。
  * 被动态代理增强的方法是切入点
* Advice（通知/增强）：
  * 所谓通知是指拦截到 Joinpoint 之后所要做的事情就是通知。
  * 通知的类型：前置通知,后置通知,异常通知,最终通知,环绕通知。

![](笔记_img/通知的类型.jpg)*

* Introduction（引介）：引介是一种特殊的通知在不修改类代码的前提下, Introduction 可以在运行期为类动态地添加一些方

法或 Field。

* Target（目标对象）：代理的目标对象

* Weaving（织入）：是指把增强应用到目标对象来创建新的代理对象的过程。spring 采用动态代理织入，而 AspectJ 采用编译期织入和类装载期织入。
  * 增强代码中的过程就是织入

* Proxy（代理）：一个类被 AOP 织入增强后，就产生一个结果代理类。

* Aspect（切面）：是切入点和通知（引介）的结合

##### 切入点表达式 / 配置AOP相关步骤：

```java
<!-- 配置srping的Ioc,把service对象配置进来-->
<bean id="accountService" class="service.impl.AccountServiceImpl"></bean>

<!--spring中基于XML的AOP配置步骤
    1、把通知Bean也交给spring来管理
    2、使用aop:config标签表明开始AOP的配置
    3、使用aop:aspect标签表明配置切面
            id属性：是给切面提供一个唯一标识
            ref属性：是指定通知类bean的Id。
    4、在aop:aspect标签的内部使用对应标签来配置通知的类型
           我们现在示例是让printLog方法在切入点方法执行之前执行：所以是前置通知
           aop:before：表示配置前置通知
                method属性：用于指定Logger类中哪个方法是前置通知
                pointcut属性：用于指定切入点表达式，该表达式的含义指的是对业务层中哪些方法增强

        切入点表达式的写法：
            关键字：execution(表达式)
            表达式：
                访问修饰符  返回值  包名.包名.包名...类名.方法名(参数列表)
            标准的表达式写法：
                public void service.impl.AccountServiceImpl.saveAccount()
            访问修饰符可以省略
                void service.impl.AccountServiceImpl.saveAccount()
            返回值可以使用通配符，表示任意返回值
                * service.impl.AccountServiceImpl.saveAccount()
            包名可以使用通配符，表示任意包。但是有几级包，就需要写几个*.
                * *.*.*.*.AccountServiceImpl.saveAccount())
            包名可以使用..表示当前包及其子包
                * *..AccountServiceImpl.saveAccount()
            类名和方法名都可以使用*来实现通配
                * *..*.*()
            参数列表：
                可以直接写数据类型：
                    基本类型直接写名称           int
                    引用类型写包名.类名的方式   java.lang.String
                可以使用通配符表示任意类型，但是必须有参数
                可以使用..表示有无参数均可，有参数可以是任意类型
            全通配写法：
                * *..*.*(..)

            实际开发中切入点表达式的通常写法：
                切到业务层实现类下的所有方法
                    * service.impl.*.*(..)
-->
<!-- 配置Logger类 -->
<bean id="logger" class="utils.Logger"></bean>

<!--配置AOP-->
<aop:config>
    <!--配置切面 -->
    <aop:aspect id="logAdvice" ref="logger">
        <!-- 配置通知的类型，并且建立通知方法和切入点方法的关联-->
        <aop:before method="printLog" pointcut="execution(* service.impl.*.*(..))"></aop:before>
    </aop:aspect>
</aop:config>
```

##### 四种常用通知类型xml配置：

```java
<!--配置AOP-->
<aop:config>
    <!--配置切面 -->
    <aop:aspect id="logAdvice" ref="logger">
        <!-- 配置通知的类型，并且建立通知方法和切入点方法的关联-->
        <!--配置了前置通知,在切入点方法执行之前执行-->
        <aop:before method="beforeprintLog" pointcut="execution(* service.impl.*.*(..))"></aop:before>
        <!--配置了后置通知，在切入点方法正常执行之后执行，它和异常通知永远只能执行一个-->
        <aop:after-returning method="afterReturningprintLog" pointcut="execution(* service.impl.*.*(..))"></aop:after-returning>
        <!--配置了异常通知，在切入点方法产生异常之后执行，它和后置通知只能执行一个-->
        <aop:after-throwing method="afterThrowingprintLog" pointcut="execution(* service.impl.*.*(..))"></aop:after-throwing>
        <!--配置了最终通知，无论切入点方法是否正常执行都会在最后执行-->
        <aop:after method="afterprintLog" pointcut="execution(* service.impl.*.*(..))"></aop:after>
        <!--配置环绕通知-->
        <aop:around method="aroundPointLog" pointcut="execution(* service.impl.*.*(..))"></aop:around>
    </aop:aspect>
</aop:config>
```

##### 基于注解AOP：

```java
@Component("logger")
@Aspect//表示当前类是一个切面类
public class Logger {
    @Pointcut("execution(* service.impl.*.*(..))")
    private void pt1(){}


    /**
     * 前置通知
     * 用于打印日志，计划在切入点方法执行之前执行（切入点方法就是业务层方法）
     */
//    @Before("pt1()")
    public void beforeprintLog(){
        System.out.println("前置通知Logger类中的beforeprintLog开始记录日志...");
    }

    /**
     * 后置通知
     */
//    @AfterReturning("pt1()")
    public void afterReturningprintLog(){
        System.out.println("后置通知Logger类中的afterReturningprintLog开始记录日志...");
    }

    /**
     * 异常通知
     */
//    @AfterThrowing("pt1()")
    public void afterThrowingprintLog(){
        System.out.println("异常通知Logger类中的afterThrowingprintLog开始记录日志...");
    }

    /**
     * 最终通知
     */
//    @After("pt1()")
    public void afterprintLog(){

        System.out.println("最终通知Logger类中的afterprintLog开始记录日志...");
    }

    /**
     * 环绕通知
     * 问题：
     *      当我们配置了环绕通知之后，切入点方法没有执行，而通知方法执行了
     * 分析：通过对比动态代理中的环绕通知代码，发现动态代理的环绕通知有明确的切入点方法的调用，而我们的代码中没有
     *
     * 解决：
     *      Spring框架为我们提供了一个接口。ProceedingJoinPoint。该接口有一个方法proceed（），此方法就相当于明确调用切入点方法
     *      该接口可以作为环绕通知的方法参数，在程序执行时，spring框架会为我们提供该接口的实现类供我们使用。
     *spring中的环绕通知：
     *      它是spring框架为我们提供一种可以在代码中手动控制增强方法合适执行的方式。
     */
    @Around("pt1()")
    public Object aroundPrintLog(ProceedingJoinPoint pjp){
        try {
            Object[] args = pjp.getArgs();
            System.out.println("环绕通知Logger类中的aroundPrintLog开始记录日志...前置");
            Object rtValue = pjp.proceed(args);//明确调用业务层方法（切入点方法）
            System.out.println("环绕通知Logger类中的aroundPrintLog开始记录日志...后置");
            return rtValue;
        } catch (Throwable throwable) {
            System.out.println("环绕通知Logger类中的aroundPrintLog开始记录日志...异常");
            throw new RuntimeException(throwable);
        }finally {
            System.out.println("环绕通知Logger类中的aroundPrintLog开始记录日志...最终");
        }
    }
```



#### Spring JDBCTemplate：

JdbcTemplate将我们使用的JDBC的流程封装起来，包括了异常的捕捉、SQL的执行、查询结果的转换等等。spring大量使用Template Method模式来封装固定流程的动作，XXXTemplate等类别都是基于这种方式的实现。 
除了大量使用Template Method来封装一些底层的操作细节，spring也大量使用callback方式类回调相关类别的方法以提供JDBC相关类别的功能，使传统的JDBC的使用者也能清楚了解spring所提供的相关封装类别方法的使用。 

**基本用法：**

```java
/**
 * JdbcTemplate的最基本用法
 */
public class JdbcTemplateDemo2 {
    public static void main(String[] args) {
        //获取容器
        ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        //获取对象
        JdbcTemplate jt  = ac.getBean("jdbcTemplate",JdbcTemplate.class);
        //执行操作
        jt.execute("insert into account1(name,money) values ('美女小妹妹',10000)");
    }
}
```

**JdbcDaoSupport：**

**第一种在** **Dao** **类中定义** **JdbcTemplate** **的方式，适用于所有配置方式（*xml和注解都可以）**

**第二种让** **Dao** **继承** **JdbcDaoSupport** **的方式，只能用于基于** **XML** **的方式，注解用不了。**

```java
/**
 * 账户的持久层实现类
 * 继承了JdbcDaoSupport以后就不能@Autowired来注解配置自动按类型注入
 * 用注解配置会比较麻烦，而可以用xml配置的方法
 */
public class AccountDaoImpl extends JdbcDaoSupport implements IAccountDao {

//    private JdbcTemplate jdbcTemplat;
//
//    public void setJdbcTemplat(JdbcTemplate jdbcTemplat) {
//        this.jdbcTemplat = jdbcTemplat;
//    }

    @Override
    public Account findAccountById(Integer accountId) {
        List<Account> accounts = super.getJdbcTemplate().query("select * from account1 where id=?", new BeanPropertyRowMapper<Account>(Account.class), accountId);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    @Override
    public Account findAccountByName(String accountName) {
        List<Account> accounts = super.getJdbcTemplate().query("select * from account1 where name =?", new BeanPropertyRowMapper<Account>(Account.class), accountName);
        if (accounts.isEmpty()) {
            return null;
        }
        if (accounts.size() > 1) {
            throw new RuntimeException("结果集不唯一");
        }
        return accounts.get(0);
    }

    @Override
    public void updateAccount(Account account) {
        super.getJdbcTemplate().update("update account1 set name=?,money=? where id=?", account.getName(), account.getMoney(), account.getId());
    }
}
```

**不继承JdbcDaoSupport：**

```java
/**
 * 账户的持久层实现类
 */
@Repository
public class AccountDaoImpl2 implements IAccountDao {

    @Autowired
    private JdbcTemplate jdbcTemplat;

//    public void setJdbcTemplat(JdbcTemplate jdbcTemplat) {
//        this.jdbcTemplat = jdbcTemplat;
//    }

    @Override
    public Account findAccountById(Integer accountId) {
        List<Account> accounts = jdbcTemplat.query("select * from account1 where id=?", new BeanPropertyRowMapper<Account>(Account.class), accountId);
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    @Override
    public Account findAccountByName(String accountName) {
        List<Account> accounts = jdbcTemplat.query("select * from account1 where name =?", new BeanPropertyRowMapper<Account>(Account.class), accountName);
        if (accounts.isEmpty()) {
            return null;
        }
        if (accounts.size() > 1) {
            throw new RuntimeException("结果集不唯一");
        }
        return accounts.get(0);
    }

    @Override
    public void updateAccount(Account account) {
        jdbcTemplat.update("update account1 set name=?,money=? where id=?", account.getName(), account.getMoney(), account.getId());
    }
}
```



#### Spring事务控制：

**PlatformTransactionManager：**

接口提供事务操作的方法，包含有3个具体的操作一获取事务状态信息

- TransactionStatus getTransaction(TransactionDefinitiondefinition)
一提交事务
- void commit(TransactionStatus status)一回滚事务
- void rollback(TransactionStatus status)

**TransactionDefinition：**

获取事务对象名称- String getName()获取事务隔离级
- int getlsolationLevel()获取事务传播行为
- int getPropagationBehavior()获取事务超时时间
- int getTimeout()获取事务是否只读
- boolean isReadOnly()（读与写事务:增加、出除·修改开启半务·兑读A事务:执行查询时，也会开启事务）

事务隔离级：反映事务提交并发访问时的处理态度------ISOLATION_DEFAULT
一默认级别，归属下列某一种- ISOLATION_READ_UNCOMMITTED
一可以读取未提交数据

- ISOLATION_READ_COMMITTED
一只能读取已提交数据，解决脏读问题(Oracle默认级别)- ISOLATION_REPEATABLE_READ
一是否读取其他事务提交修改后的数据，解决不可重复读问题(MySQL默认级别)
- ISOLATION_SERIALIZABLE
一是否读取其他事务提交添加后的数据，解决幻影读问题

**事务的传播行为：**

REQUIRED:如果当前没有事务，就新建一个事务，如果已经存在一个事务中，加入到这个事务中。一般的选

择（默认值）

SUPPORTS:支持当前事务，如果当前没有事务，就以非事务方式执行（没有事务）

MANDATORY：使用当前的事务，如果当前没有事务，就抛出异常

REQUERS_NEW:新建事务，如果当前在事务中，把当前事务挂起。

NOT_SUPPORTED:以非事务方式执行操作，如果当前存在事务，就把当前事务挂起

NEVER:以非事务方式运行，如果当前存在事务，抛出异常

NESTED:如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则执行 REQUIRED 类似的操作。

##### 基于XML的配置事务控制：

```java
<!--spring中基于XML声明式事务控制配置步骤
    1.配置事务管理器
    2.配置事务的通知
            此时我们需要导入的事务约束，tx名称空间和约束，同时也需要aop的
            使用tx:advice标签配置事务通知
            属性：id：给事务通知起一个唯一标识
                transaction-manager：给事务通知提供一个事务管理器引用
    3.配置AOP中的通用切入点表达式：
    4.建立事务通知和切入点表达式的对应关系
    5.配置事务的属性
            是在事务的通知tx:advice标签的内容

-->
<!--配置事务管理器-->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"></property>
</bean>

<!--配置事务的通知-->
<tx:advice id="txAdvice" transaction-manager="transactionManager">
    <!--配置事务的属性
    isolation：用于指定事务的隔离级别 默认值是DEFAULT，表示使用数据库的默认隔离级别
    propagation：用于指定事务的传播行为，默认值是REQUIRED，表示一定会有事务，增删改的选择，查询方法可以选择SUPPORTS
    read-only：用于指定事务是否只读，只有查询方法才可以设置为true，默认值是false，表示读写
    timeout：用于指定事务的超时时间，默认值是-1，表示永不超时，如果指定了数值，以秒为单位
    rollback-for：用于指定一个异常，当产生该异常时，事务回滚，产生其他异常时，事务不回滚，没有默认值，表示任何异常都回滚
    no-rollback-for：用于指定一个异常，当产生该异常时，事务不回滚，产生其他异常时事务回滚，没有默认值，表示任何异常都回滚
    -->
    <tx:attributes>
        <tx:method name="*" propagation="REQUIRED" read-only="false" />
        <tx:method name="find*" propagation="REQUIRED" read-only="false" />
    </tx:attributes>
</tx:advice>

<!--配置aop-->
<aop:config>
    <!--配置切入点表达式-->
    <aop:pointcut id="pt1" expression="execution(* service.impl.*.*(..))"/>
    <!--建立事务通知和切入点表达式的对应关系-->
    <aop:advisor advice-ref="txAdvice" pointcut-ref="pt1"></aop:advisor>
</aop:config>
```

##### 基于纯注解配置事务：

```java
/**
 * Spring的配置类
 * 相当于bean.xml
 */

@Configuration
@ComponentScan({"config","dao","domain","service"})
@Import({JdbcConfig.class,TransactionConfig.class})
@PropertySource("jdbcConfig.properties")//导入配置文件
@EnableTransactionManagement//开启事务的支持
public class SpringConfiguration {
}


/**
 * 和事务相关的配置类
 */

public class TransactionConfig {
    /**
     * 用于创建事务管理器对象
     * @param dataSource
     * @return
     */
    @Bean(name = "transactionManager")
    public PlatformTransactionManager createTransactionManager(DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }
}

/**
 * 和数据库连接相关的类
 */
public class JdbcConfig {

    @Value("${jdbc.driver}")
    private String driver;
    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;

    /**
     * 创建jdbcTemplate对象
     * @param dataSource
     * @return
     */
    @Bean(name = "jdbcTemplate")
    public JdbcTemplate createJdbcTemplate(DataSource dataSource){
        return  new JdbcTemplate(dataSource);
    }

    /**
     * 创建一个数据源对象
     * @return
     */
    @Bean(name = "dataSource")
    public DataSource createDataSource(){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }
}
```