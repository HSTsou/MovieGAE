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
			areaName = "基隆";
			break;		
		case "a02":
			areaName = "台北";
			break;		
		case "a02z1":
			areaName = "台北東區";
			break;
		case "a02z2":
			areaName = "台北西區";
			break;
		case "a02z3":
			areaName = "台北南區";
			break;
		case "a02z4":
			areaName = "台北北區";
			break;
		case "a02z5":
			areaName = "新北市";
			break;
		case "a02z6":
			areaName = "台北二輪";
			break;	
		case "a03":
			areaName = "桃園";
			break;
		case "a04":
			areaName = "台中";
			break;
		case "a05":
			areaName = "嘉義";
			break;
		case "a06":
			areaName = "台南";
			break;
		case "a07":
			areaName = "高雄";
			break;
		case "a35":
			areaName = "新竹";
			break;
		case "a37":
			areaName = "苗栗";
			break;
		case "a38":
			areaName = "花蓮";
			break;
		case "a39":
			areaName = "宜蘭";
			break;
		case "a45":
			areaName = "雲林";
			break;
		case "a47":
			areaName = "彰化";
			break;
		case "a49":
			areaName = "南投";
			break;
		case "a68":
			areaName = "金門";
			break;
		case "a69":
			areaName = "澎湖";
			break;
		case "a87":
			areaName = "屏東";
			break;
		case "a89":
			areaName = "台東";
			break;

		default:
			break;
		}

		return areaName;

	}

}
