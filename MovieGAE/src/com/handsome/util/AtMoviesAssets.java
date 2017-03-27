package com.handsome.util;

public class AtMoviesAssets {

	public AtMoviesAssets() {
		// TODO Auto-generated constructor stub
	}

	
	public String getAreaNameById(String str) {
		
		String formatArea = str.substring(str.lastIndexOf("/") - 3,
				str.lastIndexOf("/"));//ex:/showtime/flcn74027270/a02/
		//System.out.println(formatArea);
		String areaName = null;
		
		switch (formatArea) {
		case "a01":
			areaName = "��";
			break;		
		case "a02":
			areaName = "�x�_";
			break;		
		case "a02z1":
			areaName = "�x�_�F��";
			break;
		case "a02z2":
			areaName = "�x�_���";
			break;
		case "a02z3":
			areaName = "�x�_�n��";
			break;
		case "a02z4":
			areaName = "�x�_�_��";
			break;
		case "a02z5":
			areaName = "�s�_��";
			break;
		case "a02z6":
			areaName = "�x�_�G��";
			break;	
		case "a03":
			areaName = "���";
			break;
		case "a04":
			areaName = "�x��";
			break;
		case "a05":
			areaName = "�Ÿq";
			break;
		case "a06":
			areaName = "�x�n";
			break;
		case "a07":
			areaName = "����";
			break;
		case "a35":
			areaName = "�s��";
			break;
		case "a37":
			areaName = "�]��";
			break;
		case "a38":
			areaName = "�Ὤ";
			break;
		case "a39":
			areaName = "�y��";
			break;
		case "a45":
			areaName = "���L";
			break;
		case "a47":
			areaName = "����";
			break;
		case "a49":
			areaName = "�n��";
			break;
		case "a68":
			areaName = "����";
			break;
		case "a69":
			areaName = "���";
			break;
		case "a87":
			areaName = "�̪F";
			break;
		case "a89":
			areaName = "�x�F";
			break;

		default:
			break;
		}

		return areaName;

	}

}
