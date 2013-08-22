<?php
 
/*
 * Following code will create a new profile row
 * All profile details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
require_once '/home/thesis-std/request_key.php';
 
// check for required fields
if (isset($_POST['userID1']) && isset($_POST['userID2']) && isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY) {
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    $userID1 = mysql_real_escape_string($_POST['userID1']);
    $userID2 = mysql_real_escape_string($_POST['userID2']);
 
	$result_opponent_details = mysql_query("SELECT up.firstName, up.lastName, up.pictureURL
FROM userprofiles up
WHERE up.userID = $userID2");

	if(!empty($result_opponent_details)) {
		if(mysql_num_rows($result_opponent_details) > 0){
			$result_opponent_details = mysql_fetch_array($result_opponent_details);
			
			$opponentDetails = array();
			$opponentDetails["opponentID"] = $userID2;
			$opponentDetails["firstName"] = $result_opponent_details["firstName"];
            $opponentDetails["lastName"] = $result_opponent_details["lastName"];
			$opponentDetails["pictureURL"] = $result_opponent_details["pictureURL"];
			
			$response["opponentDetails"] = array();
            array_push($response["opponentDetails"], $opponentDetails);
		}
	}
 
 
    // mysql inserting a new row
	$result = mysql_query("INSERT INTO soundbattles(userID1,userID2) VALUES('$userID1', '$userID2')");
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
		$response["soundBattleID"] = mysql_insert_id();
        $response["message"] = "Sound battle successfully created with id: ".$response["soundBattleID"].".";
 
        // echoing JSON response
        echo json_encode($response);
		
		//send notification
		$regId_result = mysql_query("SELECT gu.gcm_regid FROM gcm_users gu WHERE gu.userID = $userID2");
		$firstName_result = mysql_query("SELECT up.firstName FROM userprofiles up WHERE up.userID = $userID1");
		if(!empty($regId_result) && !empty($firstName_result)){
			$regId_array = mysql_fetch_array($regId_result);
			$regId = $regId_array["gcm_regid"];
			$firstName_array = mysql_fetch_array($firstName_result);
			$firstName = $firstName_array["firstName"];
			$message = "$firstName challenged you for a Sound Battle!";

			include_once 'gcm/GCM.php';

			$gcm = new GCM();

			$registatoin_ids = array($regId);
			$message = array("price" => $message);

			$result = $gcm->send_notification($registatoin_ids, $message);
		}

		
		
		
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>