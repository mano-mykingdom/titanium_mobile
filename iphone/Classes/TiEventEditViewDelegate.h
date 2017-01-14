//
//  TiEventEditViewDelegate.h
//  Test
//
//  Created by Manojkumar on 1/10/17.
//
//

#import <EventKit/EventKit.h>
#import <EventKitUI/EKEventEditViewController.h>
#import "TiCalendarEvent.h"

@interface TiEventEditViewDelegate : NSObject<EKEventEditViewDelegate> {
@private
    BOOL closed;
    KrollCallback* callback;
}

-(id)initWithCallback:(KrollCallback*)callback_;

@end