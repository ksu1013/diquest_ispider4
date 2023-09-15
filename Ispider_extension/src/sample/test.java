package sample;

public class test {
	public static void main(String[] args) {
		
		String checkName="ㄹr인 ZX884 조건만남";
		String[] kkk4= {"조건만남","마사지","페이만남","출장마사지","애인대행","출장안마"};
		for (int i = 0; i < kkk4.length; i++) {
			if(checkName.indexOf(kkk4[i])>-1) {
				System.out.println("저장안함");
			}else {
				System.out.println("저장 함");
			}
		}
		
	}
}
