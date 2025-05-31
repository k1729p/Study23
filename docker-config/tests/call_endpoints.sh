#!/usr/bin/env bash
SITE="http://gateway:8081/api/document"
BRIGHT_RED='\033[1;31m'
BRIGHT_GREEN='\033[1;32m'
#BRIGHT_BLUE='\033[1;34m'
BRIGHT_CYAN='\033[1;36m'
#BRIGHT_MAGENTA='\033[1;35m'
BRIGHT_YELLOW='\033[1;33m'
#BRIGHT_WHITE='\033[1;37m'
NC='\033[0m' # No Color
USERS=("alice" "bob" "charlie" "david")
ENDPOINTS=("official" "restricted" "confidential" "secret")

for USER_NAME in "${USERS[@]}"; do
    echo -e "${BRIGHT_YELLOW}----------------------------------------------------------------------${NC}"
    echo -e "${BRIGHT_GREEN}User $USER_NAME accessing the endpoints${NC}"
    echo -e "${BRIGHT_YELLOW}----------------------------------------------------------------------${NC}"

    ACCESS_TOKEN=$("$JAVA_HOME/bin/java" "Extractor.java" "$USER_NAME" | awk -F'=' '/ACCESS_TOKEN/{print $2}')
    if [ -z "$ACCESS_TOKEN" ]; then
        echo -e "${BRIGHT_RED}Error: Failed to extract access token for user $USER_NAME.${NC}"
        continue
    fi
    SUCCESS_COUNT=0
    FAILURE_COUNT=0
    for ENDPOINT in "${ENDPOINTS[@]}"; do
        echo -e "${BRIGHT_CYAN}Accessing $ENDPOINT document for user $USER_NAME${NC}"
        RESPONSE=$(curl -s -H "Authorization: Bearer $ACCESS_TOKEN" "$SITE/$ENDPOINT")
        if [ -n "$RESPONSE" ]; then
            echo "$RESPONSE"
            ((SUCCESS_COUNT++))
        else
            echo -e "${BRIGHT_RED}████▌HTTP ERROR 403▐████▌No authorization to access the document.▐████${NC}"
            ((FAILURE_COUNT++))
        fi
        echo -e "${BRIGHT_YELLOW}----------------------------------------------------------------------${NC}"
    done

    echo -e "${BRIGHT_GREEN}User $USER_NAME finished accessing endpoints: $SUCCESS_COUNT successful and $FAILURE_COUNT failed attempts${NC}"
    echo -e "${BRIGHT_YELLOW}----------------------------------------------------------------------${NC}"
    echo
done
echo -e "${BRIGHT_RED}----------------------------------------------------------------------${NC}"
echo -e "${BRIGHT_RED}FINISH${NC}"