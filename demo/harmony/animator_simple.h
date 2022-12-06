/*
 * Copyright (c) 2021-2022 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


#include <list>



namespace OHOS::Ace {

class ACE_EXPORT Animator : public AceType, public StatusListenable {
    DECLARE_ACE_TYPE(Animator, AceType);

public:

    Animator(const char* name = nullptr);

    ~Animator() override;


    void RemoveInterpolator(const RefPtr<Interpolator>& animation);
    bool IsStopped() const;
};

} // namespace OHOS::Ace

#endif // FOUNDATION_ACE_FRAMEWORKS_CORE_ANIMATION_ANIMATOR_H
