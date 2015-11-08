package TripleLift.Server;

public class TestCases {
	
	public static void main(String[] args) {
		testCase01();
		testCase02();
		testCase03();
		testCase04();
	}

	// Single Input ID. 
	public static void testCase01(){
		TripleLiftServer server = new TripleLiftServer();
		String inputJson = "[{\"advertiser_id\":\"123\",\"ymd\":\"2015-11-08\",\"num_clicks\":11,\"num_impressions\":1683},{\"advertiser_id\":\"123\",\"ymd\":\"2015-11-07\",\"num_clicks\":3,\"num_impressions\":1995}]";
		server.createDataMap(inputJson);
		String output = server.output();
		String expectedOutput = "2015-11-08: Impressions = 1683, Clicks = 11\n2015-11-07: Impressions = 1995, Clicks = 3\n";
		checkOutput(1, output, expectedOutput);
	}
	
	// Multiple Input IDs for different dates per advertiser. 
	public static void testCase02(){
		TripleLiftServer server = new TripleLiftServer();
		String inputJson1 = "[{\"advertiser_id\":\"123\",\"ymd\":\"2015-11-08\",\"num_clicks\":11,\"num_impressions\":400},{\"advertiser_id\":\"123\",\"ymd\":\"2015-11-06\",\"num_clicks\":3,\"num_impressions\":200}]";
		server.createDataMap(inputJson1);
		String inputJson2 = "[{\"advertiser_id\":\"124\",\"ymd\":\"2015-11-08\",\"num_clicks\":19,\"num_impressions\":800},{\"advertiser_id\":\"124\",\"ymd\":\"2015-11-07\",\"num_clicks\":5,\"num_impressions\":100}]";
		server.createDataMap(inputJson2);
		String output = server.output();
		String expectedOutput = "2015-11-08: Impressions = 1200, Clicks = 30\n2015-11-07: Impressions = 100, Clicks = 5\n2015-11-06: Impressions = 200, Clicks = 3\n";
		checkOutput(2, output, expectedOutput);
	}
	
	
	// JSON data in multiple rows and a unformatted manner.
	public static void testCase03(){
		TripleLiftServer server = new TripleLiftServer();
		String inputJson1 = "[{\"advertiser_id\"           :\"123  \",  \n  \"ymd\":\"2015-11-08\",  \n \"num_clicks\":11,\"num_impressions\":400},{\"advertiser_id\":\"123\",\"ymd\":\"2015-11-06\",\"num_clicks\":3,\"num_impressions\":200}]";
		server.createDataMap(inputJson1);
		String inputJson2 = "[  \n   {\"advertiser_id\":  \n  \"124\",\"ymd\":\"2015-11-08\",\"num_clicks\":19,\"num_impressions\":800},{\"advertiser_id\":\"124\",\"ymd\":\"2015-11-07\",\"num_clicks\":5,\"num_impressions\":100}]";
		server.createDataMap(inputJson2);
		String output = server.output();
		String expectedOutput = "2015-11-08: Impressions = 1200, Clicks = 30\n2015-11-07: Impressions = 100, Clicks = 5\n2015-11-06: Impressions = 200, Clicks = 3\n";
		checkOutput(3, output, expectedOutput);
	}
	
	// Server timeout test.
	public static void testCase04(){
		TripleLiftServer server = new TripleLiftServer();
		server.setTimeout(10); // Set to zero to emulate timeout.
		server.fetchData(new long[] {123, 789});
		String output = server.getLog();
		String expectedOutput = "The data for advertiser ID: 123 could not be retrieved.\nThe data for advertiser ID: 789 could not be retrieved.\n";
		checkOutput(4, output, expectedOutput);
	}
	
	private static void checkOutput(int no, String output, String expectedOutput){
		if(output.equals(expectedOutput)){
			System.out.println(" - Testcase " + no + " passed.");
		}
		else{
			System.out.println(" - Testcase " + no + " failed. X");
		}
	}
}