package ua.nure.antoniuk.Practice8.db;

import ua.nure.antoniuk.Practice8.entity.Group;
import ua.nure.antoniuk.Practice8.entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Max on 21.12.2017.
 */
public class DBManager {
    private static final String URL = "jdbc:mysql://localhost/epamprictice8?user=root&password=root&useSSL=false";

    private static final String SQL_FIND_USER_BY_LOGIN = "SELECT * FROM users WHERE login=?";

    private static final String SQL_CREATE_NEW_USER =
            "INSERT INTO users VALUES (DEFAULT, ?)";

    private static final String SQL_FIND_ALL_USERS = "SELECT * FROM users";

    private static final String SQL_DELETE_USER = "DELETE FROM users WHERE id=?";
    private static final String SQL_GET_GROUP_BY_ID = "SELECT * FROM groups WHERE id=?";

    private static final String SQL_UPDATE_USER = "UPDATE users SET login=?, name=? WHERE id=?";
    private static final String SQL_CREATE_NEW_GROUP = "INSERT INTO groups VALUES (DEFAULT, ?)";
    private static final String SQL_FIND_ALL_GROUPS = "SELECT * FROM `groups`";
    private static final String SQL_FIND_GROUP_BY_NAME = "SELECT * FROM groups WHERE `name`=?";
    private static final String ADD_USERS_GROUP = "INSERT INTO users_groups VALUES(?,?)";
    private static final String SQL_FIND_ALL_USERS_GROUPS = "SELECT * FROM `users_groups` WHERE user_id=?";
    private static final String SQL_DELETE_GROUP = "DELETE FROM groups WHERE id = ?";
    private static final String SQL_UPDATE_GROUP = "UPDATE groups SET `name`=? WHERE id=?";

    ///////////////////////////

    private static DBManager instance;

    public static synchronized DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    private DBManager() {
        // nothing to do
    }

    public Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(URL);
        // ...
        return con;
    }

    //////////////////////////////////////////////////


    public User getUser(String login) throws SQLException, DBException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(SQL_FIND_USER_BY_LOGIN);

            int k = 1;
            pstmt.setString(k++, login);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DBException("Cannot obtain a user by login", ex);
        } finally {
            close(rs);
            close(pstmt);
            close(con);
        }
        return null;
    }

    public Group getGroup(String name) throws SQLException, DBException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(SQL_FIND_GROUP_BY_NAME);

            int k = 1;
            pstmt.setString(k++, name);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractGroup(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DBException("Cannot obtain a group by name", ex);
        } finally {
            close(rs);
            close(pstmt);
            close(con);
        }
        return null;
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setLogin(rs.getString("login"));
        return user;
    }

    public boolean insertUser(User user) throws DBException {
        boolean res = false;

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(SQL_CREATE_NEW_USER,
                    Statement.RETURN_GENERATED_KEYS);

            int k = 1;
            pstmt.setString(k++, user.getLogin());
            int id = getId(pstmt);
            if (id > 0) {
                user.setId(id);
                res = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DBException("Can not create a user:" +  user, ex);
        } finally {
            close(con);
        }
        return res;
    }

    public boolean insertGroup(Group group) throws DBException {
        boolean res = false;

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(SQL_CREATE_NEW_GROUP,
                    Statement.RETURN_GENERATED_KEYS);

            int k = 1;
            pstmt.setString(k++, group.getName());
            int id = getId(pstmt);
            if (id > 0) {
                group.setId(id);
                res = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DBException("Can not create a group:" +  group, ex);
        } finally {
            close(con);
        }
        return res;
    }

    private int getId(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = null;
        int res = 0;
        if (pstmt.executeUpdate() > 0) {
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                res = rs.getInt(1);
            }
        }
        close(rs);
        return res;
    }

    private void close(AutoCloseable ac) {
        if (ac != null) {
            try {
                ac.close();
            } catch (Exception ex) {
                throw new IllegalStateException("Can not close " + ac);
            }
        }
    }

    public List<User> findAllUsers() throws DBException {
        List<User> users = new ArrayList<>();

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.createStatement();

            rs = stmt.executeQuery(SQL_FIND_ALL_USERS);

            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DBException("Cannot obtain a user by login", ex);
        } finally {
            close(con);
        }
        return users;
    }

    public List<Group> findAllGroups() throws DBException {
        List<Group> users = new ArrayList<>();

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.createStatement();

            rs = stmt.executeQuery(SQL_FIND_ALL_GROUPS);

            while (rs.next()) {
                users.add(extractGroup(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DBException("Cannot obtain a user by login", ex);
        } finally {
            close(con);
        }
        return users;
    }

    private Group extractGroup(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setId(rs.getInt(1));
        group.setName(rs.getString("name"));
        return group;
    }

    public boolean setGroupsForUser(User user, Group... team) throws DBException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean res = false;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(ADD_USERS_GROUP);

            stmt.setInt(1, user.getId());

            for (Group g : team) {
                stmt.setInt(2, g.getId());
                stmt.executeUpdate();
                res = true;
            }

            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            customRollback(con);
            throw new DBException("Can not add users groups", e);
        }

        return res;
    }

    public List<String> getUserGroups(User user) throws DBException {
        List<String> users = new ArrayList<>();

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.prepareStatement(SQL_FIND_ALL_USERS_GROUPS);
            stmt.setInt(1, user.getId());
            rs = stmt.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append(getGroupById(rs.getInt("group_id")).getName()).append(" ");

            }
            users.add(sb.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DBException("Cannot obtain a users group by login", ex);
        } finally {
            close(con);
        }
        return users;
    }

    private Group getGroupById(int id) throws DBException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(SQL_GET_GROUP_BY_ID);

            int k = 1;
            pstmt.setInt(k++, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractGroup(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DBException("Cannot obtain a group by name", ex);
        } finally {
            close(rs);
            close(pstmt);
            close(con);
        }
        return null;
    }

    private void customRollback(Connection connection) throws DBException {
        if (Objects.isNull(connection)) {
            throw new DBException("Connection is null in rollback", null);
        }
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBException("Can not rollback operation", e);
        }
    }

    public boolean deleteGroup(Group team) throws DBException {
        boolean res = false;

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(SQL_DELETE_GROUP);

            pstmt.setInt(1, team.getId());
            if (pstmt.executeUpdate() > 0) {
                res = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DBException("Can not delete a group:" +  team, ex);
        } finally {
            close(con);
        }
        return res;
    }

    public boolean updateGroup(Group team) throws DBException {
        boolean res = false;

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(SQL_UPDATE_GROUP);

            pstmt.setInt(2, team.getId());
            pstmt.setString(1, team.getName());
            if (pstmt.executeUpdate() > 0) {
                res = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DBException("Can not update a group:" +  team, ex);
        } finally {
            close(con);
        }
        return res;
    }
}
