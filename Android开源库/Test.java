
public interface APIService {
  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);
}

// 添加对 RxJava 支持后


