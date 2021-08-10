package cn.itcast.wanxinp2p.consumer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@TableName("consumer")
public class Consumer implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * ����
	 */
	@TableId(value = "ID", type = IdType.AUTO)
	private Long id;

	/**
	 * �û���
	 */
	@TableField("USERNAME")
	private String username;

	/**
	 * ��ʵ����
	 */
	@TableField("FULLNAME")
	private String fullname;

	/**
	 * ���֤��
	 */
	@TableField("ID_NUMBER")
	private String idNumber;

	/**
	 * �û�����,����Ψһ,�û��ڴ��ϵͳ��ʶ
	 */
	@TableField("USER_NO")
	private String userNo;

	/**
	 * ƽ̨Ԥ���ֻ���
	 */
	@TableField("MOBILE")
	private String mobile;

	/**
	 * �û�����,����or��ҵ��Ԥ��
	 */
	@TableField("USER_TYPE")
	private String userType;

	/**
	 * �û���ɫ.�����orͶ����
	 */
	@TableField("ROLE")
	private String role;

	/**
	 * �����Ȩ�б�
	 */
	@TableField("AUTH_LIST")
	private String authList;

	/**
	 * �Ƿ��Ѱ����п�
	 */
	@TableField("IS_BIND_CARD")
	private Integer isBindCard;

	/**
	 * ����״̬
	 */
	@TableField("STATUS")
	private Integer status;

	/**
	 * �ɴ����
	 */
	@TableField("LOAN_AMOUNT")
	private BigDecimal loanAmount;

	/**
	 * ������ˮ��
	 */
	@TableField("REQUEST_NO")
	private String requestNo;

}
