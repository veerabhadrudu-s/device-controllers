/*
 * Author: sveera
 */

import { BannerComponent } from './banner.component';
import { TestBed, ComponentFixture, async } from '@angular/core/testing';
import { BaseHrefProviderService } from '../../services/base.href.service';
import { RouterTestingModule } from '@angular/router/testing';

fdescribe('Test cases related to ' + BannerComponent.name, () => {
    let bannerComponentFixture: ComponentFixture<BannerComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [RouterTestingModule],
            declarations: [BannerComponent],
            providers: [BaseHrefProviderService]
        });
        bannerComponentFixture = TestBed.createComponent(BannerComponent);
    });

    fit('Is ' + BannerComponent.name + ' not null', async(() => {
        const bannerComponent: BannerComponent = bannerComponentFixture.componentInstance;
        bannerComponentFixture.whenStable().then(() => {
            bannerComponentFixture.detectChanges();
            expect(bannerComponent).not.toBeNull();
        });
    }));

});
