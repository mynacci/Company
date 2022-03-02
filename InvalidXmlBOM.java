class InvalidXmlBOM
{
	/*
		XML에서 BOM 캐릭터를 제거하기 위한 예제
	*/

	public static void main(String[] args) 
	{
		String xml10pattern = "[^"
			+ "\u0009\r\n"
			+ "\u0020-\uD7FF"
			+ "\uE000-\uFFFD"
			+ "\ud800\udc00-\udbff\udfff"
			+ "]";


		// XML 1.1
		// [#x1-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
		String xml11pattern = "[^"
			+ "\u0001-\uD7FF"
			+ "\uE000-\uFFFD"
			+ "\ud800\udc00-\udbff\udfff"
			+ "]+";

		String illegal = "안녕하세요 BOM 캐릭터 테스트 파일입니다     <\uFFFE>         <\uFFFE>     BOM";
		String legal = illegal.replaceAll(xml10pattern, " ");

		System.out.println(legal);
	}
}
