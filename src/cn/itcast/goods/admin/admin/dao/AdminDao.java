package cn.itcast.goods.admin.admin.dao;

import cn.itcast.goods.admin.admin.domain.Admin;
import cn.itcast.jdbc.TxQueryRunner;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

/**
 * Created by yanke on 2016/12/2.
 */
public class AdminDao {
    QueryRunner queryRunner =new TxQueryRunner();

    /**
     * 根据用户名和密码查询用户是否存在；
     * @param adminname
     * @param adminpwd
     * @return
     */
    public Admin find(String adminname,String adminpwd) throws SQLException {

        String sql = "select * from t_admin where adminname=? and adminpwd=?";
        return queryRunner.query(sql,new BeanHandler<Admin>(Admin.class),adminname,adminpwd);

    }
}
