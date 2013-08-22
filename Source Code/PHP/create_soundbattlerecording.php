<?php
 
/*
 * Following code will create a new profile row
 * All profile details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
require_once '/home/thesis-std/request_key.php';
 
// check for required fields
if (isset($_POST['userID']) && isset($_POST['soundBattleLocationID']) && isset($_POST['noiseRecordingID']) && isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY){
  
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
	
    $userID = mysql_real_escape_string($_POST['userID']);
    $soundBattleLocationID = mysql_real_escape_string($_POST['soundBattleLocationID']);
	$noiseRecordingID = mysql_real_escape_string($_POST['noiseRecordingID']);
 
    // mysql inserting a new row

	$result = mysql_query("INSERT INTO soundbattlerecordings(userID,soundBattleLocationID,noiseRecordingID) VALUES('$userID', '$soundBattleLocationID', '$noiseRecordingID')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Sound battle recording successfully created.";
 
        // echoing JSON response
        echo json_encode($response);
		
		
		$soundbattleid_result = mysql_query("SELECT sbl.soundBattleID FROM soundbattlelocations sbl WHERE sbl.soundBattleLocationID = $soundBattleLocationID");
		$soundbattleid_array = mysql_fetch_array($soundbattleid_result);
		$soundbattleid = $soundbattleid_array["soundBattleID"];
		$nbsoundbattlerecordings_result = mysql_query("SELECT COUNT(sbr.noiseRecordingID) FROM soundbattlerecordings sbr JOIN soundbattlelocations sbl ON sbl.soundBattleLocationID = sbr.soundBattleLocationID WHERE sbl.soundBattleID = $soundbattleid");
		$nbsoundbattlerecordings_array = mysql_fetch_array($nbsoundbattlerecordings_result);
		$nbsoundbattlerecordings = $nbsoundbattlerecordings_array["COUNT(sbr.noiseRecordingID)"];
		if($nbsoundbattlerecordings == 3){
			//send notification
			
			$regId_result = mysql_query("SELECT gu.gcm_regid FROM gcm_users gu JOIN soundbattles sb ON sb.userID1 = gu.userID WHERE sb.soundBattleID = $soundbattleid AND sb.userID1 != $userID UNION SELECT gu.gcm_regid FROM gcm_users gu JOIN soundbattles sb ON sb.userID2 = gu.userID WHERE sb.soundBattleID = $soundbattleid AND sb.userID2 != $userID");
			$firstName_result = mysql_query("SELECT up.firstName FROM userprofiles up WHERE up.userID = $userID");
			if(!empty($regId_result) && !empty($firstName_result)){
				$regId_array = mysql_fetch_array($regId_result);
				$regId = $regId_array["gcm_regid"];
				$firstName_array = mysql_fetch_array($firstName_result);
				$firstName = $firstName_array["firstName"];
				$message = "$firstName finished a Sound Battle against you!";

				include_once 'gcm/GCM.php';

				$gcm = new GCM();

				$registatoin_ids = array($regId);
				$message = array("price" => $message);

				$result = $gcm->send_notification($registatoin_ids, $message);
				
			}
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