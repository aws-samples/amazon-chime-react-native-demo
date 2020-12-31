/**
 * @format
 */

import 'react-native';
import React from 'react';
import { Login } from '../src/containers/Login';

// Note: test renderer must be required after react-native.
import renderer from 'react-test-renderer';

test('renders correctly', () => {
  const tree = renderer.create(<Login />).toJSON();
  // jest snapshot testing: https://jestjs.io/docs/en/snapshot-testing
  expect(tree).toMatchSnapshot()
});
