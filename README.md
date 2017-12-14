

# spring-boot-starter-spring-jtt
# on spring JdbcTemplate made a package


```java
@SpringBootApplication
@EnableDruid
@EnableSpringJtt
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
}

```



The parent of this project can be modified by itself. I use the upper POM.xml.
```java
<parent>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-parent</artifactId>
	<version>1.5.6.RELEASE</version>
</parent>
```




It is recommended to use the following items to make your project a bit more dive

* [spring-boot-starter-druid](https://github.com/15174834/spring-boot-starter-druid) 



Donation developer (ETH)<br>
0x23b96A20Fae711ED6D286feAEED437a6831e3dD7