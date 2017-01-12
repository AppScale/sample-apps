// test that go vendoring is working

package vendortestcode

import (
	"fmt"
	"net/http"

	"my.vendor.test/foo/bar"
)

func init() {
	http.HandleFunc("/", vendortest)
}

func vendortest(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Go vendoring is %s.", bar.Working)
}
