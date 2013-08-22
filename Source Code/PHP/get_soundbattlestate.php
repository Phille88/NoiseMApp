<?php 
// array for JSON response
$response = array();
require_once '/home/thesis-std/request_key.php';
 
// check for post data
if (isset($_POST['userID']) && isset($_POST['soundBattleID']) && isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY) {
 
	// include db connect class
	require_once __DIR__ . '/db_connect.php';
	 
	// connecting to db
	$db = new DB_CONNECT();


    $userID = mysql_real_escape_string($_POST['userID']);
	$soundBattleID = mysql_real_escape_string($_POST['soundBattleID']);
 
    // get a userprofile from userprofiles table
    $result_all_locations = mysql_query("SELECT soundBattleLocationID, longitude, latitude FROM soundbattlelocations WHERE soundBattleID = $soundBattleID");
	$result_recorded_locations = mysql_query("SELECT soundbattlelocations.soundBattleLocationID
FROM soundbattlelocations
JOIN soundbattlerecordings
ON soundbattlerecordings.soundBattleLocationID = soundbattlelocations.soundBattleLocationID
WHERE soundBattleID = $soundBattleID AND userID = $userID");
	$result_opponent_details = mysql_query("SELECT up.userID, up.firstName, up.lastName, up.pictureURL
FROM userprofiles up
JOIN (SELECT sb.userID1
FROM soundbattles sb
WHERE sb.soundBattleID = $soundBattleID AND sb.userID2 = $userID
UNION
SELECT sb.userID2
FROM soundbattles sb
WHERE sb.soundBattleID = $soundBattleID AND sb.userID1 = $userID) sbs
ON sbs.userID1 = up.userID");

	if(!empty($result_opponent_details)) {
		if(mysql_num_rows($result_opponent_details) > 0){
			$result_opponent_details = mysql_fetch_array($result_opponent_details);
			
			$opponentDetails = array();
			$opponentDetails["userID"] = $result_opponent_details["userID"];
			$opponentDetails["firstName"] = $result_opponent_details["firstName"];
            $opponentDetails["lastName"] = $result_opponent_details["lastName"];
			$opponentDetails["pictureURL"] = $result_opponent_details["pictureURL"];
			$response["opponentDetails"] = array();
            array_push($response["opponentDetails"], $opponentDetails);
		}
	}

	$recorded_soundBattleIDs = array();
	if(!empty($result_recorded_locations)) {
		if(mysql_num_rows($result_all_locations) > 0){
			while($row = mysql_fetch_array($result_recorded_locations)){
				array_push($recorded_soundBattleIDs, $row["soundBattleLocationID"]);
			}
		}
	}
 
    if (!empty($result_all_locations)) {
        // check for empty result
        if (mysql_num_rows($result_all_locations) > 0) {
			$response["soundbattlelocations"] = array();
			
            while($row = mysql_fetch_array($result_all_locations)){
				$soundbattlelocation = array();
				$soundbattlelocation["soundBattleLocationID"] = $row["soundBattleLocationID"];
				$soundbattlelocation["longitude"] = $row["longitude"];
				$soundbattlelocation["latitude"] = $row["latitude"];
				$soundbattlelocation["recorded"] = false;
				if(in_array($row["soundBattleLocationID"], $recorded_soundBattleIDs)) {
					$soundbattlelocation["recorded"] = true;
				}
				array_push($response["soundbattlelocations"],$soundbattlelocation);
			}
            // success
            $response["success"] = 1;
 
            // echoing JSON response
            echo json_encode($response);
        } else {
		
			$response["soundbattlelocations"] = array();
			$soundbattlelocation=array();
			array_push($response["soundbattlelocations"],$soundbattlelocation);
			
            $response["success"] = 1;
            $response["message"] = "No sound battle locations found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no userprofile found
        $response["success"] = 0;
        $response["message"] = "No sound battle state found";
 
        // echo no users JSON
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