config.files.push({
	pattern: "kotlin/composeResources/**/*",
	included: false,
	served: true,
	watched: false,
});
config.proxies["/composeResources/"] = "/base/kotlin/composeResources/";
