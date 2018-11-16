package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.util.Base64.Encoder;

public class CategoryDAO {

	public static final String DERBY_DRIVER = "org.apache.derby.jdbc.ClientDriver";
	public static final String DB_URL = "jdbc:derby://localhost:64413/EECS;user=student;password=secret";
	public static final String ALL_CATEGORIES_QUERY = "SELECT * FROM CATEGORY";
	public static final String SINGLE_CATEGORY_QUERY = "SELECT * FROM CATEGORY WHERE ID = ?";

	private Connection con;
	private Encoder picEncoder;

	public CategoryDAO() {

		try {
			Class.forName(DERBY_DRIVER).newInstance();
			con = DriverManager.getConnection(DB_URL);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		picEncoder = Base64.getEncoder();
	}

	public List<CategoryBean> getAllCategories() throws Exception {

		Statement s = con.createStatement();
		s.executeUpdate("set schema roumani");

		PreparedStatement preS;
		preS = con.prepareStatement(ALL_CATEGORIES_QUERY);

		ResultSet r = preS.executeQuery();

		List<CategoryBean> categoryList = makeCategoryList(r);
		return categoryList;
	}

	public CategoryBean getCategory(int catId) throws SQLException {
		Statement s = con.createStatement();
		s.executeUpdate("set schema roumani");

		PreparedStatement preS;
		preS = con.prepareStatement(SINGLE_CATEGORY_QUERY);
		preS.setInt(1, catId);

		ResultSet r = preS.executeQuery();
		r.next();

		CategoryBean category = setCategoryBean(r);
		return category;
	}

	private List<CategoryBean> makeCategoryList(ResultSet r) throws SQLException {

		List<CategoryBean> categoryList = new ArrayList<>();

		while (r.next()) {
			CategoryBean category = setCategoryBean(r);

			categoryList.add(category);
		}
		return categoryList;
	}

	private CategoryBean setCategoryBean(ResultSet r) throws SQLException {
		CategoryBean category = new CategoryBean();
		category.setDescription(r.getString("DESCRIPTION"));
		category.setName(r.getString("NAME"));
		category.setId(r.getInt("ID"));
		category.setPicture(getCategoryPicture(r.getBytes("PICTURE")));

		return category;
	}

	private String getCategoryPicture(byte[] picture) {
		byte[] encoded = picEncoder.encode(picture);

		String encodedString = new String(encoded);

		return encodedString;
	}

}