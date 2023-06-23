## TaskReport
CLI using Gradle Enterprise API. This tool will retrieve different reports based on a task path:
* Duration of the task when is executed
* Task information by cache outcome
* Graph image with the task duration
* Csv file with the task duration

Example
```
 ./taskreport -api-key=$GE_KEY --url=$GE_URL --project=acme --requested-task=assemble --task-path=:core:compileKotlin --tags=ci
```

Required parameters:
* `api-key` Gradle Enterprise API token
* `url` Gradle Enterprise instance
* `project` Project where the build is executed
* `requested-task` Requested task of the build
* `task-path` Task path under investigation
* `tags` tags used in the build

#### Using Binary
Github release latest version contains the Project Report binary. After downloading the binary you can execute:
```
 ./taskreport -api-key=$GE_KEY --url=$GE_URL --max-builds=20000 --project=nowinandroid --requested-task=assemble --task-path=:core:model:compileKotlin --tags=ci
```

##### You can build from sources using this repository:
```
./gradlew install
cd build/install/taskreport/bin
./taskreport -api-key=$GE_KEY --url=$GE_URL --max-builds=20000 --project=nowinandroid --requested-task=assemble --task-path=:core:model:compileKotlin --tags=ci
```

##### Output
###### Report
Available at the end of the execution
```
┌──────────────────────────────────────┐
│       Task Cache State Report        │
├──────────────────────────────────────┤
│ assemble > :core:model:compileKotlin │
├──────────────────────────────────────┤
│              tags: [ci]              │
├─────────────────────────────┬────────┤
│ executed_cacheable          │    578 │
├─────────────────────────────┼────────┤
│ avoided_from_local_cache    │   2824 │
└─────────────────────────────┴────────┘
┌───────────────────────────────────────────────────────────────────────────────┐
│                  Task Duration Report with outcome executed                   │
├───────────────────────────────────────────────────────────────────────────────┤
│                     assemble > :core:model:compileKotlin                      │
├───────────────────────────────────────────────────────────────────────────────┤
│                                  tags: [ci]                                   │
├────────┬────────┬────────────┬────────────┬─────────┬────────────┬────────────┤
│ Builds │  Mean  │    P25     │    P50     │   P75   │    P90     │    P99     │
├────────┼────────┼────────────┼────────────┼─────────┼────────────┼────────────┤
│  578   │ 18.83s │ 15.907250s │ 18.909500s │ 21.013s │ 24.315300s │ 28.400870s │
└────────┴────────┴────────────┴────────────┴─────────┴────────────┴────────────┘


```
###### Image
The TaskReport will generate an image with the build duration of the tasks executed
![](images/duration_core_model_compileKotlin-1687558225852.png)

###### Csv file
The TaskReport will generate a csv file with the build id, date and duration of the tasks executed:
```
BuildId,Date,Duration
7111xk22z57ay,1683179400735,20849
59566565xx3j4,1683179441258,25344
12kj21kj21jk6,1683179884655,19457
rxhc0a9jq101e,1683179936085,19525
3232kl32ss2l2,1683179949069,20756
kkajjwq2isdis,1683179951921,19808
wqklqwlkqwlkq,1683179958387,27682


```
#### Parameters

| Name                  | Description                                | Default | Required | Example                               |
|-----------------------|--------------------------------------------|---------|----------|---------------------------------------|
| api-key               | String token                               |         | Yes      | --api-key=$TOKEN_FILE                 |
| url                   | Gradle Enterprise instance                 |         | Yes      | --url=https://ge.acme.dev             |
| project               | Root project in Gradle Enterprise          |         | Yes      | --project=acme                        |
| requested-task        | Requested task in the build                |         | Yes      | --requested-task=assemble             |
| task-path             | Task complete path                         |         | Yes      | --task-path=:core:model:compileKotlin |
| tags                  | Tags used in the build scans to process    | empty   | Yes      | --tags=ci --tags=linux                |
| max-builds            | Max builds to be processed                 | 1000    | No       | --max-builds=2000                     |
| since-build-id        | Starting build to apply the reverse search | null    | No       | --since-build-id=cqiqsDqa2m7cw        |
| include-failed-builds | Include failing builds                     | true    | No       | --include-failed-builds               |



#### Compatibility
We have tested the tool with Java 8, 11 and 17.
