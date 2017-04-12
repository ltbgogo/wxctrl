package wxctrl;

import java.util.Arrays;

import com.abc.wxctrl.repository.RepoFactory;

public class JpaManagerTest {

	public static void main(String[] args) throws Exception {
		RepoFactory.f.getWxUserRepo().findAll();
	}
}
