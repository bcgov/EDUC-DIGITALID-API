# Ministry of Education PEN Registry
Replacement of current personal education number (PEN) registry

The main function of the PEN Registry system is to process (School/PSI) requests for PENs, issue new PENs where no PEN currently exists for the student and provide PEN matches for those students who have been provided PENs previously. PEN data is used for BC provincial exams, graduation credentials, and research and reporting.  Once issued, the PEN follows the student through their BC education path from Early Learning, through K-12 and Post-Secondary enrollment. The PEN is used for multiple purposes including; the distribution of funding to schools, transition analysis between schools, districts and Post-Secondary instructions, as well as for provincial exams, scholarships and for student mark reporting.

## Directory Structure

    .github/                   - PR and Issue templates
    api/                       - API codebase
    frontend/                  - Frontend codebase
    tools/                     - Devops utilities
    └── jenkins                - Jenkins standup
    Jenkinsfile                - Top-level Pipeline
    Jenkinsfile.cicd           - Pull-Request Pipeline
    LICENSE                    - License

## Documentation

* [Openshift Readme](openshift/README.md)
* [PEN Registry Wiki](https://github.com/bcgov/EDUC-PEN/wiki)

## Getting Help or Reporting an Issue

To report bugs/issues/features requests, please file an [issue](https://github.com/bcgov/EDUC-PEN/issues).

## License

    Copyright 2019 Province of British Columbia

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.