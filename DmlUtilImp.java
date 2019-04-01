package com.java.dao.imp;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 曾冠超
 * @version 1.0
 * @ClassName: AAA
 * @Description: TODO
 * @date: 2018/
 * 基于mysql数据库操作语言的util
 * 主要针对于添加和修改语句的便捷生成
 * 主要使用反射原理反射javabean对象判断传入对象的值生成语句
 */
public class DmlUtilImp<T> {
    private final static DmlUtilImp dm = new DmlUtilImp<>();
    private T t;
    Field[] Fields ;
    public DmlUtilImp(){
    }

    public static DmlUtilImp getInstance() {
        return dm;
    }

    /**
     * sql修改语句
     * 根据传入对象判断
     * 如果javabean属性判断,如果值不为null生成对应查询语句
     *
     * @param t   传入修改的对象
     * @param sql 查询数据表
     *            默认情况下获得首个属性为主键id进行sql语句条件修改
     * @return
     */
    public String updatel(T t, String sql) {
        Fields = t.getClass().getDeclaredFields();
        //生成属性数组
        StringBuffer sb = new StringBuffer("update " + sql + " set ");
        //遍历数组获得属性名
        List<String> list = getNames(t);
        for (String s : list) {
            sb.append(s + "=?,");
        }
        String substring = sb.substring(0, sb.length() - 1);
        sb = new StringBuffer(substring);
        sb.append(" where " + Fields[0].getName() + "=? ");
        return sb.toString();
    }

    /**
     * sal添加语句
     * 根据传入对象判断
     * 如果javabean属性判断,如果值不为null生成对应查询语句
     *
     * @param t   传入修改的对象
     * @param sql 查询数据表
     * @return
     */
    public String insertl(T t, String sql) {
        Fields = t.getClass().getDeclaredFields();
        Class c = t.getClass();
        //生成属性数组
        StringBuffer sb = new StringBuffer("insert into " + sql + " (");
        //遍历数组获得属性名
        List<String> list = getNames(t);
        for (int i = 0; i < list.size() - 1; i++) {
            sb.append(list.get(i) + ", ");
        }
        sb.append(list.get(list.size() - 1) + ")");
        sb.append(" values (");
        for (int i = 0; i < list.size() - 1; i++) {
            sb.append("?,");
        }
        sb.append("?)");
        return sb.toString();
    }
    /**
     * 获得删除语句
     * @param sql 数据库对应表
     * @param id 删除条件
     * @return
     */
    public String delete(String sql, String id) {
        return "DELETE FROM " + sql + " where " + id + "=? ";
    }

    /**
     * 获得查询语句
     * @param sql 数据库对应表
     * @param id  查询条件
     * @return
     */
    public String select(String sql, String id) {
        Fields = t.getClass().getDeclaredFields();
        return "select* FROM " + sql + " where " + id + "=? ";
    }

    /**
     * 获得不为空值属性名集合
     * @param t
     * @return
     */
    public List<String> getNames(T t) {
        Fields = t.getClass().getDeclaredFields();
        String name1 = t.getClass().getName();
        List<String> list=new LinkedList<>();
        for (int i = 0; i < Fields.length; i++) {
            Field field = Fields[i];
            field.setAccessible(true);
            String s = field.getType().toString();

            String name = field.getName();
            try {
                Object o = field.get(t);
                //如果不为null和\u0000加入集合
                if (o instanceof Character) {
                    Character charo = (Character) o;
                    if (charo == '\u0000') {
                        continue;
                    }
                }

                if (o != null) {
                    list.add(name);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 获得对象不为空值的值集合
     * @param t
     * @return 值集合
     */
    public List getValues(T t) {
        Fields = t.getClass().getDeclaredFields();
        List list=new LinkedList();
        for (int i = 0; i < Fields.length; i++) {
            Field field = Fields[i];
            field.setAccessible(true);
            String name = field.getName();
            try {
                Object o = field.get(t);
                //如果不为null和\u0000加入集合
                if (o instanceof Character) {
                    Character charo = (Character) o;
                    if (charo == '\u0000') {
                        continue;
                    }
                }
                if (o != null) {
                    if (o instanceof Date) {
                        SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");
                        String format = datef.format((Date) o);
                        list.add(format);
                        continue;
                    } else {
                        list.add(o);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    public static void main(String[] args) {

        DmlUtilImp.getInstance().getNames(new User(12));
    }
}
