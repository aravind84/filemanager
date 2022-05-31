app_ver := 1.0

GO_PATH:=$(shell go env GOPATH)
PATH:=${PATH}:${GO_PATH}/bin
export PATH

kind_install: ## Install kind tool
	go install sigs.k8s.io/kind@v0.14.0

k8s_creation: ## Create local k8s cluster with kind and local container repository
	./kind-cluster.sh

build_app: ## Build the spring boot application via gradle
	./gradlew -PprojVersion=$(app_ver) clean build

docker_build_push: build_app  ## Build docker image for the built springboot application
	docker build -t filemanager:$(app_ver) --build-arg appVersion=$(app_ver) . 
	docker tag filemanager:$(app_ver) localhost:5001/filemanager:$(app_ver)
	docker push localhost:5001/filemanager:$(app_ver)
	
deploy_monitoring: ## Deploy Prometheus and Grafana monitoring components into the k8s cluster
	helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
	helm repo update
	pwd
	helm -n monitoring install prometheus prometheus-community/kube-prometheus-stack --create-namespace -f helm_chart/custom-prometheus-values.yaml --debug

deploy_app: ## Deploy application helm chart into the k8s cluster
	helm -n filemanager install filemanager helm_chart/ --create-namespace --debug

clean: ## Cleanup the stack and cluster
	helm -n filemanager uninstall filemanager
	helm -n monitoring uninstall prometheus
	${GO_PATH}/bin/kind delete cluster

.PHONY: help

help: ## Help commands
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
