#!/usr/bin/env bash

SCENARIO=$1
DIRECTORY="./docker_results"
COMMAND="commands_${SCENARIO}.txt"
RESULTS="drone_delivery_${SCENARIO}_results.txt"
INITIAL="drone_delivery_initial_${SCENARIO}_results.txt"
DIFFERENCE="diff_results_${SCENARIO}.txt"
TIMEFORMAT="%R"

mkdir -p ${DIRECTORY}

function dockerstuff(){
  docker exec dronedelivery sh -c "\
    java -jar drone_delivery.jar < ${COMMAND} > ${RESULTS} && \
    diff -s ${RESULTS} ${INITIAL} > ${DIFFERENCE}"
  docker cp dronedelivery:/usr/src/cs6310/${RESULTS} ${DIRECTORY}
  docker cp dronedelivery:/usr/src/cs6310/${DIFFERENCE} ${DIRECTORY}
}

if [[ -f "./test_scenarios/${COMMAND}" ]]; then
  docker run -d --name dronedelivery gatech/dronedelivery sh -c "mkdir docker_results && sleep 100"  > /dev/null
  RUNTIME=`time (dockerstuff) 2>&1`
  docker rm -f dronedelivery > /dev/null
  FILE_CONTENTS="${DIRECTORY}/${DIFFERENCE}"
  echo "Elapsed time: ${RUNTIME}s" >> ${FILE_CONTENTS}
  echo "$(cat ${FILE_CONTENTS})"
else
    echo "File ${COMMAND} does not exist."
fi