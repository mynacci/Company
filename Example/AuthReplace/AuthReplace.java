import java.sql.*;
import com.threesoft.jbulker.idol.database.IdolCustomRowProcess;

/* 권한변경에 따른 replace 일괄 처리를 위한 JBulker 커스텀 클래스 예제 */
/* TODO. DB 계정정보 및 큐테이블명 수정 */
public class AuthReplace extends IdolCustomRowProcess {
	Connection connection = null;

	public void init() {
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		String jbdcUrl = "jdbc:jtds:sqlserver://192.168.0.105:1433/test;instance=TEST";
		String dbUser = "sa";
		String dbPass = "saadmin";

		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(jbdcUrl, dbUser, dbPass);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void process() {
		String queueTable = "queue_table";
		
		PreparedStatement st = null;
		try {
			String sql = "delete FROM " + queueTable + " where vdkkey=?";

			st = connection.prepareStatement(sql);
			st.setString(1, getFieldValue("DREREFERENCE"));
			int r = st.executeUpdate();

			System.out.println("deleted queue row : " + r);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void close() {
		try {
			if(connection != null) connection.close();
		} catch(SQLException se) {
			se.printStackTrace();
		}
	}

}