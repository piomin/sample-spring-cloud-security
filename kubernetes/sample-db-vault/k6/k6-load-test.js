import http from 'k6/http';
import { check } from 'k6';

export default function () {
  const res = http.get(`http://sample-app-service-demo-apps.apps.cluster-dzgkp.dzgkp.sandbox1982.opentlc.com/persons`);
  check(res, {
    'is status 200': (res) => res.status === 200,
    'body size is > 0': (r) => r.body.length > 0,
  });
}