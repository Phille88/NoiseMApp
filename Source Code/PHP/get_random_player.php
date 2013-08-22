<?php 
// array for JSON response
$response = array();
 
require_once '/home/thesis-std/request_key.php';
 
// check for post data
if (isset($_POST['userID']) && isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY) {
 
	// include db connect class
	require_once __DIR__ . '/db_connect.php';
	// connecting to db
	$db = new DB_CONNECT();
	
    $userID = mysql_real_escape_string($_POST['userID']);
 
    // get a random userprofile number from userprofiles table
  
	$result_userIDs = mysql_query( "SELECT userID FROM userprofiles WHERE userID != $userID");

	if(!empty($result_userIDs)) {
		if (mysql_num_rows($result_userIDs) > 0){
			$userIDs = array();
			while ($row = mysql_fetch_array($result_userIDs)){
				array_push($userIDs, $row["userID"]);
			}
			$randomUserIDindex = rand(0,sizeof($userIDs)-1);
			$randomUserID = $userIDs[$randomUserIDindex];
	
			$result = mysql_query( "SELECT userID FROM userprofiles WHERE userID = $randomUserID");
			if (!empty($result)) {
				// check for empty result
				if (mysql_num_rows($result) > 0) {
		 
					$result = mysql_fetch_array($result);
					// success
					$response["success"] = 1;
		 
					// user node
					$response["opponentID"] = $result["userID"];
		 
					// echoing JSON response
					echo json_encode($response);
				} else {
					// no userprofile found
					$response["success"] = 0;
					$response["message"] = "No user profile found";
		 
					// echo no users JSON
					echo json_encode($response);
				}
			} else {
				// no userprofile found
				$response["success"] = 0;
				$response["message"] = "No user profile found";
		 
				// echo no users JSON
				echo json_encode($response);
			}
			
		}
	}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>