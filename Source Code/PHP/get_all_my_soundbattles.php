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
 
    // get a soundbattle from soundbattles table
    $result = mysql_query("(SELECT ownrecordings.soundBattleID, ownAmountNRs, up.firstName AS oppFirstName, oppAmountNRs
FROM (                  (SELECT DISTINCT      sb.soundBattleID AS soundBattleID, sb.userID1 AS ownID, COUNT(sbr.noiseRecordingID) AS ownAmountNRs
                        FROM 		      soundbattles sb
                        LEFT JOIN	      soundbattlelocations sbl ON sb.soundBattleID = sbl.soundBattleID
                        LEFT JOIN	      soundbattlerecordings sbr ON (sbl.soundBattleLocationID = sbr.soundBattleLocationID AND sb.userID1 = sbr.userID)
                        WHERE		      sb.userID1 = $userID
                        GROUP BY	      sb.soundBattleID) AS ownrecordings
        JOIN
                        (SELECT DISTINCT      sb.soundBattleID AS soundBattleID, sb.userID2 AS oppID, COUNT(sbr.noiseRecordingID) AS oppAmountNRs
                        FROM 		      soundbattles sb
                        LEFT JOIN	      soundbattlelocations sbl ON sb.soundBattleID = sbl.soundBattleID
                        LEFT JOIN	      soundbattlerecordings sbr ON (sbl.soundBattleLocationID = sbr.soundBattleLocationID AND sb.userID2 = sbr.userID)
                        WHERE		      sb.userID1 = $userID
                        GROUP BY	      sb.soundBattleID) AS opprecordings
         ON      ownrecordings.soundBattleID = opprecordings.soundBattleID), userprofiles up
WHERE   up.userID = oppID)
UNION
(SELECT ownrecordings.soundBattleID, ownAmountNRs, up.firstName AS oppFirstName, oppAmountNRs
FROM (                  (SELECT 	DISTINCT      sb.soundBattleID AS soundBattleID, sb.userID2 AS ownID, COUNT(sbr.noiseRecordingID) AS ownAmountNRs
                        FROM 		      soundbattles sb
                        LEFT JOIN	      soundbattlelocations sbl ON sb.soundBattleID = sbl.soundBattleID
                        LEFT JOIN	      soundbattlerecordings sbr ON (sbl.soundBattleLocationID = sbr.soundBattleLocationID AND sb.userID2 = sbr.userID)
                        WHERE		      sb.userID2 = $userID
                        GROUP BY	      sb.soundBattleID) AS ownrecordings
        JOIN
                        (SELECT DISTINCT      sb.soundBattleID AS soundBattleID, sb.userID1 AS oppID, COUNT(sbr.noiseRecordingID) AS oppAmountNRs
                        FROM 		      soundbattles sb
                        LEFT JOIN	      soundbattlelocations sbl ON sb.soundBattleID = sbl.soundBattleID
                        LEFT JOIN             soundbattlerecordings sbr ON (sbl.soundBattleLocationID = sbr.soundBattleLocationID AND sb.userID1 = sbr.userID)
                        WHERE		      sb.userID2 = $userID
                        GROUP BY	      sb.soundBattleID) AS opprecordings
         ON      ownrecordings.soundBattleID = opprecordings.soundBattleID), userprofiles up
WHERE   up.userID = oppID)");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
			$response["soundbattles"] = array();
			$soundbattles["opensoundbattles"] = array();
			$soundbattles["pendingsoundbattles"] = array();
			$soundbattles["finishedsoundbattles"] = array();
			
			while ($row = mysql_fetch_array($result)){
				if($row["ownAmountNRs"] < 3) {
					$opensoundbattle = array();
					$opensoundbattle["soundBattleID"] = $row["soundBattleID"];
					$opensoundbattle["oppFirstName"] = $row["oppFirstName"];
					$opensoundbattle["ownAmountNRs"] = $row["ownAmountNRs"];
				
					array_push($soundbattles["opensoundbattles"], $opensoundbattle);
				}
				else if($row["ownAmountNRs"] >= 3 && $row["oppAmountNRs"] < 3) {
					$pendingsoundbattle = array();
					$pendingsoundbattle["soundBattleID"] = $row["soundBattleID"];
					$pendingsoundbattle["oppFirstName"] = $row["oppFirstName"];
					$pendingsoundbattle["ownAmountNRs"] = $row["ownAmountNRs"];
				
					array_push($soundbattles["pendingsoundbattles"], $pendingsoundbattle);
				}
				else if($row["ownAmountNRs"] >= 3 && $row["oppAmountNRs"] >= 3) {
					$finishedsoundbattle = array();
					$finishedsoundbattle["soundBattleID"] = $row["soundBattleID"];
					$finishedsoundbattle["oppFirstName"] = $row["oppFirstName"];
					$finishedsoundbattle["ownAmountNRs"] = $row["ownAmountNRs"];
				
					array_push($soundbattles["finishedsoundbattles"], $finishedsoundbattle);
				}
			}
			$response["soundbattles"] = $soundbattles;
	
            // success
            $response["success"] = 1;
 
            // echoing JSON response
            echo json_encode($response);
        } 
		else {
            // no soundbattles found
            $response["success"] = 0;
            $response["message"] = "No Sound Battles found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no soundbattles found
        $response["success"] = 0;
        $response["message"] = "No Sound Battles found";
 
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