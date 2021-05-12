import dal.dao.UserDao;
import entities.User;
import entities.UserType;

public class Main {
  public static void main(String[] args) {
    UserDao dao = new UserDao();
    for (int i = 0; i < 20; i++) {
      User user = new User("hifffffkfi"+i, "hihi", UserType.STANDARD, "LKDSJF");
      dao.add(user);
    }
  }
}