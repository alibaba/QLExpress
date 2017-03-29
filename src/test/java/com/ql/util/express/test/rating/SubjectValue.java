package com.ql.util.express.test.rating;

/**
 * 科目数据
 * @author xuannan
 *
 */
public class SubjectValue {
  public Object userId;
  public Object subjectId;
  public double value;
  public String toString(){
	  return "科目[" + userId + "," + subjectId +"] = " + value;
  }
}
