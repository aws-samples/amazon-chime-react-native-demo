/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 */

const SERVER_URL = 'YOUR_SERVER_URL';
const SERVER_REGION = 'YOUR_SERVER_REGION'

export function createMeetingRequest(meetingName, attendeeName) {

  let url = encodeURI(SERVER_URL + "/join?" + `title=${meetingName}&name=${attendeeName}&region=${SERVER_REGION}`);

  return fetch(url, { method: 'POST' }).then(j => j.json());
}
